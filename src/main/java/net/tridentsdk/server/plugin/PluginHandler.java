/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.tridentsdk.server.plugin;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.tridentsdk.Trident;
import net.tridentsdk.concurrent.HeldValueLatch;
import net.tridentsdk.concurrent.SelectableThread;
import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.event.Listener;
import net.tridentsdk.plugin.Plugin;
import net.tridentsdk.plugin.PluginLoadException;
import net.tridentsdk.plugin.Plugins;
import net.tridentsdk.plugin.annotation.IgnoreRegistration;
import net.tridentsdk.plugin.annotation.PluginDesc;
import net.tridentsdk.plugin.cmd.Command;
import net.tridentsdk.registry.Registered;
import net.tridentsdk.server.concurrent.ConcurrentTaskExecutor;
import net.tridentsdk.server.concurrent.TickSync;
import net.tridentsdk.util.TridentLogger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Handles server plugins, loading and unloading, class management, and lifecycle management for plugins
 * <p>
 * <p>To access this handler, use this code:
 * <pre><code>
 *     PluginHandler handler = Registered.plugins();
 * </code></pre></p>
 *
 * @author The TridentSDK Team
 * @since 0.3-alpha-DP
 */
public class PluginHandler extends ForwardingList<Plugin> implements Plugins {
    private static final SelectableThread EXECUTOR = ConcurrentTaskExecutor.create(1, "Plugins").selectCore();
    final Map<String, Plugin> plugins = Maps.newConcurrentMap(); // This need not be concurrent... but TridentLogger >.<

    /**
     * Do not instantiate this without being Trident
     * <p>
     * <p>To access this handler, use this code:
     * <pre><code>
     *     TridentPluginHandler handler = Handler.plugins();
     * </code></pre></p>
     */
    public PluginHandler() {
        if (!Trident.isTrident())
            throw new RuntimeException(new IllegalAccessException("Can only be instantiated by Trident"));
    }

    @Override
    @InternalUseOnly
    public Plugin load(final File pluginFile) {
        HeldValueLatch<Plugin> latch = HeldValueLatch.create();

        TickSync.sync(new Runnable() {
            @Override
            public void run() {
                JarFile jarFile = null;
                try {
                    // load all classes
                    jarFile = new JarFile(pluginFile);
                    PluginClassLoader loader = new PluginClassLoader(pluginFile, getClass().getClassLoader());
                    Class<? extends Plugin> pluginClass = null;

                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();

                        if (entry.isDirectory() || !entry.getName().endsWith(".class")) {
                            continue;
                        }

                        String name = entry.getName().replace(".class", "").replace('/', '.');
                        Class<?> loadedClass = loader.loadClass(name);

                        loader.putClass(loadedClass);

                        if (Plugin.class.isAssignableFrom(loadedClass)) {
                            if (pluginClass != null)
                                TridentLogger.error(new PluginLoadException("Plugin has more than one main class!"));

                            pluginClass = loadedClass.asSubclass(Plugin.class);
                        }
                    }

                    // start initiating the plugin class and registering commands and listeners
                    if (pluginClass == null) {
                        TridentLogger.error(new PluginLoadException("Plugin does not have a main class"));
                        loader.unloadClasses();
                        loader = null; // help gc
                        return;
                    }

                    PluginDesc description = pluginClass.getAnnotation(PluginDesc.class);

                    if (description == null) {
                        TridentLogger.error(new PluginLoadException("PluginDesc annotation does not exist!"));
                        loader.unloadClasses();
                        loader = null; // help gc
                        return;
                    }

                    if (plugins.containsKey(description.name())) {
                        TridentLogger.error(new PluginLoadException("Plugin with name " + description.name() +
                                " has been loaded"));
                        loader.unloadClasses();
                        loader = null; // help gc
                        return;
                    }

                    TridentLogger.log("Loading " + description.name() + " version " + description.version());

                    Plugin plugin = pluginClass.newInstance();
                    plugin.init(pluginFile, description, loader);
                    plugins.put(description.name(), plugin);
                    plugin.load();
                    latch.countDown(plugin);
                    TridentLogger.success("Loaded " + description.name() + " version " + description.version());
                } catch (IOException | IllegalAccessException | InstantiationException | ClassNotFoundException ex) { // UNLOAD PLUGIN
                    TridentLogger.error(new PluginLoadException(ex));
                } finally {
                    if (jarFile != null)
                        try {
                            jarFile.close();
                        } catch (IOException e) {
                            TridentLogger.error(e);
                        }
                }
            }
        });

        try {
            return latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void enable(Plugin plugin) {
        TridentLogger.log("Enabling " + plugin.description().name() + " version " + plugin.description().version());
        for (Class<?> cls : plugin.classLoader.loadedClasses().values()) {
            try {
                register(plugin, cls, EXECUTOR);
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

        TickSync.sync(plugin::enable);
        TridentLogger.success("Enabled " + plugin.description().name() + " version " + plugin.description().version());
    }

    private void register(Plugin plugin, Class<?> cls, SelectableThread executor) throws InstantiationException {
        if (Modifier.isAbstract(cls.getModifiers()))
            return;

        Object instance = null;
        Constructor<?> c = null;

        try {
            if (!cls.isAnnotationPresent(IgnoreRegistration.class)) {
                if (Listener.class.isAssignableFrom(cls)) {
                    c = cls.getConstructor();
                    Registered.events().registerListener(plugin, (Listener) (instance = c.newInstance()));
                }

                if (Command.class.isAssignableFrom(cls)) {
                    if (c == null)
                        c = cls.getConstructor();
                    Registered.commands().register(plugin, (Command) (instance == null ? c.newInstance() : instance));
                }
            }
        } catch (NoSuchMethodException e) {
            TridentLogger.error(
                    new PluginLoadException("A no-arg constructor for class " + cls.getName() + " does not exist"));
        } catch (IllegalAccessException e) {
            TridentLogger.error(
                    new PluginLoadException("A no-arg constructor for class " + cls.getName() + " is not accessible"));
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disable(final Plugin plugin) {
        TickSync.sync(() -> {
            // Perform disabling first, we don't want to unload everything
            // then disable it
            // State checking could be performed which breaks the class loader
            plugin.disable();

            plugins.remove(plugin.description().name());

            plugin.classLoader.unloadClasses();
            plugin.classLoader = null;
        });
    }

    @Override
    protected List<Plugin> delegate() {
        return ImmutableList.copyOf(plugins.values());
    }

    @Override
    public SelectableThread executor() {
        return EXECUTOR;
    }
}