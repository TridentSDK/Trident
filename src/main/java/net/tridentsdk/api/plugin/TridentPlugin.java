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
package net.tridentsdk.api.plugin;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import net.tridentsdk.api.Trident;
import net.tridentsdk.api.config.JsonConfig;
import net.tridentsdk.api.plugin.annotation.PluginDescription;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class TridentPlugin {
    private static final HashFunction HASHER = Hashing.murmur3_32();

    final PluginClassLoader classLoader;
    private final File pluginFile;
    private final File configDirectory;
    private final PluginDescription description;
    private final JsonConfig defaultConfig;

    protected TridentPlugin() {
        this.pluginFile = null;
        this.description = null;
        this.defaultConfig = null;
        this.configDirectory = null;
        this.classLoader = null;
    } // avoid any plugin initiation outside of this package

    TridentPlugin(File pluginFile, PluginDescription description, PluginClassLoader loader) {
        for (TridentPlugin plugin : Trident.getServer().getPluginHandler().getPlugins()) {
            if (plugin.getDescription().name().equalsIgnoreCase(description.name())) {
                throw new IllegalStateException("Plugin already initialized or plugin with this name already exists! " +
                        "Name: " + description.name());
            }
        }

        this.pluginFile = pluginFile;
        this.description = description;
        this.configDirectory = new File("plugins/" + description.name() + '/');
        this.defaultConfig = new JsonConfig(new File(this.configDirectory, "config.json"));
        this.classLoader = loader;
    }

    public void onEnable() {
        // Method intentionally left blank
    }

    public void onLoad() {
        // Method intentionally left blank
    }

    public void onDisable() {
        // Method intentionally left blank
    }

    final void startup() {
        // TODO
    }

    public void saveDefaultConfig() {
        this.saveResource("config.json", false);
    }

    public void saveResource(String name, boolean replace) {
        try {
            InputStream is = this.getClass().getResourceAsStream('/' + name);
            File file = new File(this.configDirectory, name);

            if (is == null) {
                return;
            }

            if (replace && file.exists()) {
                file.delete();
            }

            Files.copy(is, file.getAbsoluteFile().toPath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public final File getFile() {
        return this.pluginFile;
    }

    public JsonConfig getDefaultConfig() {
        return this.defaultConfig;
    }

    public File getConfigDirectory() {
        return this.configDirectory;
    }

    public final PluginDescription getDescription() {
        return this.description;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof TridentPlugin) {
            TridentPlugin otherPlugin = (TridentPlugin) other;
            if (otherPlugin.getDescription().name().equals(this.getDescription().name())) {
                if (otherPlugin.getDescription().author().equals(this.getDescription().author())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        // Find constants
        String name = this.getDescription().name();
        String author = this.getDescription().author();

        return HASHER.newHasher()
                .putUnencodedChars(name)
                .putUnencodedChars(author)
                .hash().hashCode();
    }

    // TODO: override hashvalue as well
}
