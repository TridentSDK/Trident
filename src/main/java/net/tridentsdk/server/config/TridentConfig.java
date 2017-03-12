/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
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
package net.tridentsdk.server.config;


import com.google.gson.JsonObject;
import net.tridentsdk.config.Config;
import net.tridentsdk.config.ConfigSection;

import javax.annotation.concurrent.Immutable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The default implementation of a configuration file that
 * is loaded into memory.
 *
 * @author TridentSDK
 * @since 0.5-alpha
 */
@Immutable
public class TridentConfig extends TridentConfigSection implements Config {
    /**
     * The mapping of configs cached by the server.
     *
     * Configs should really only have one instance so this
     * cache holds configs indefinitely.
     */
    private static final ConcurrentMap<Path, TridentConfig> cachedConfigs = new ConcurrentHashMap<>();

    /**
     * Releases the configuration file that may be cached
     * at the given location.
     *
     * @param path the path to evict the config
     */
    public static void release(Path path) {
        cachedConfigs.remove(path);
    }

    /**
     * The path to the config file
     */
    private final Path path;

    /**
     * Creates a new config from the given path
     *
     * @param path the path to the config file
     */
    protected TridentConfig(Path path) {
        super("", null, null);
        this.path = path;
    }

    /**
     * Init safety static factory method; instance of this
     * class is published when creating new config sections
     * via the load method in TridentConfigSection
     */
    public static TridentConfig load(Path path) {
        return cachedConfigs.computeIfAbsent(path, k -> {
            TridentConfig config = new TridentConfig(path);
            try {
                config.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return config;
        });
    }

    @Override
    public File getFile() {
        return this.path.toFile();
    }

    @Override
    public Path getPath() {
        return this.path;
    }

    @Override
    public File getDirectory() {
        return this.path.getParent().toFile();
    }

    @Override
    public ConfigSection getRoot() {
        return this;
    }

    @Override
    public ConfigSection getParent() {
        return this;
    }

    @Override
    public void load() throws IOException {
        JsonObject object = ConfigIo.readConfig(this.path);
        this.read(object);
    }

    @Override
    public void save() throws IOException {
        JsonObject object = this.write();
        ConfigIo.writeConfig(this.path, object);
    }
}
