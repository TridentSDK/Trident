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
package net.tridentsdk.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PluginClassLoader extends URLClassLoader {
    final Map<String, Class<?>> classes = new ConcurrentHashMap<>();
    private Class<? extends TridentPlugin> pluginClass;

    public PluginClassLoader(File pluginFile) throws MalformedURLException {
        super(new URL[]{pluginFile.toURI().toURL()});
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return this.loadClass(name, true);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (name.startsWith("net.tridentsdk")) {
            throw new ClassNotFoundException(name);
        }
        Class<?> result = this.classes.get(name);

        if (result == null) {
            result = super.loadClass(name, resolve);

            if (result == null) {
                if (resolve) {
                    try {
                        result = Class.forName(name);
                    } catch (ClassNotFoundException ignored) {
                    }
                }
            }
        }

        if (result != null) {
            if (TridentPlugin.class.isAssignableFrom(result)) {
                if (this.pluginClass != null) {
                    throw new PluginLoadException("JAR has 2 plugin classes!");
                }

                this.pluginClass = result.asSubclass(TridentPlugin.class);
            }

            this.classes.put(result.getName(), result);

            return result;
        }

        throw new ClassNotFoundException(name);
    }

    public void unloadClasses() {
        for (Class<?> cls : this.classes.values()) {
            // TODO: unload class
        }
    }

    public Class<? extends TridentPlugin> getPluginClass() {
        return this.pluginClass;
    }
}
