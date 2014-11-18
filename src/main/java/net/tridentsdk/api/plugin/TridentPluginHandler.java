/*
 *     TridentSDK - A Minecraft Server API
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.api.plugin;

import net.tridentsdk.api.Trident;
import net.tridentsdk.api.event.Listener;
import net.tridentsdk.api.reflect.FastClass;
import net.tridentsdk.api.plugin.annotation.IgnoreRegistration;
import net.tridentsdk.api.plugin.annotation.PluginDescription;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class TridentPluginHandler {
    private final List<TridentPlugin> plugins = new ArrayList<>();

    public void load(File pluginFile) {
        JarFile jarFile = null;
        try {
            // load all classes
            PluginClassLoader loader = new PluginClassLoader(pluginFile);
            jarFile = new JarFile(pluginFile);
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();

                if (!entry.getName().endsWith(".class")) {
                    continue;
                }

                loader.loadClass(entry.getName().replace('/', '.'));
            }

            // start initiating the plugin class and registering commands and listeners
            Class<? extends TridentPlugin> pluginClass = loader.getPluginClass();
            PluginDescription description = pluginClass.getAnnotation(PluginDescription.class);

            if (description == null) {
                throw new PluginLoadException("Description annotation does not exist!");
            }

            Constructor<? extends TridentPlugin> defaultConstructor =
                    pluginClass.getConstructor(File.class, PluginDescription.class);
            final TridentPlugin plugin = defaultConstructor.newInstance(pluginFile, description);

            this.plugins.add(plugin);

            for (Class<?> cls : plugin.classLoader.classes.values()) {
                if (Listener.class.isAssignableFrom(cls) && !cls.isAnnotationPresent(IgnoreRegistration.class)) {
                    FastClass fastClass = FastClass.get(cls);
                    Listener listener = fastClass.getConstructor().newInstance();

                    Trident.getServer().getEventManager().registerListener(listener);
                }

                //TODO: register commands
            }

            Trident.getServer().provideThreads().providePluginThread(plugin).addTask(new Runnable() {
                @Override
                public void run() {
                    plugin.startup();
                }
            });
        } catch (IOException | ClassNotFoundException | NoSuchMethodException
                | IllegalAccessException | InvocationTargetException | InstantiationException ex) {
            throw new PluginLoadException(ex);
        } finally {
            if (jarFile != null)
                try {
                    jarFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public void disable(TridentPlugin plugin) {
        plugin.onDisable();

        this.plugins.remove(plugin);
        plugin.classLoader.unloadClasses();
    }

    public Iterable<TridentPlugin> getPlugins() {
        return Collections.unmodifiableList(this.plugins);
    }
}
