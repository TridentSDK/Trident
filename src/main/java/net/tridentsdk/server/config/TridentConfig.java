/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * The default implementation of a configuration file that
 * is loaded into memory.
 *
 * @author TridentSDK
 * @since 0.5-alpha
 */
public class TridentConfig extends TridentConfigSection implements Config {
    /**
     * The path to the config file
     */
    private final Path path;

    /**
     * Creates a new config from the given path
     *
     * @param path the path to the config file
     */
    public TridentConfig(Path path) {
        this.path = path;

        try {
            this.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public File file() {
        return path.toFile();
    }

    @Override
    public Path path() {
        return this.path;
    }

    @Override
    public File directory() {
        return path.getParent().toFile();
    }

    @Override
    public void load() throws IOException {
        JsonObject object = ConfigIo.readConfig(this.path);
        read(object);
    }

    @Override
    public void save() throws IOException {
        JsonObject object = new JsonObject();
        for (ConfigSection section : children(true)) {
            TridentConfigSection tSec = (TridentConfigSection) section;
            tSec.write(object);
        }

        ConfigIo.writeConfig(this.path, object);
    }
}