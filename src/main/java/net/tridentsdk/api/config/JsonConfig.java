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
package net.tridentsdk.api.config;

import com.google.common.base.Charsets;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

// TODO: Javadoc

/**
 * Represents the root ConfigSection of a Configuration file Controls all IO actions of the file
 *
 * @author The TridentSDK Team
 */
public class JsonConfig extends ConfigSection {
    private final Path path;

    /**
     * Creates a new JSON configuration file from NIO path
     *
     * @param path the NIO path for file directory
     */
    public JsonConfig(Path path) {
        this.path = path;
        this.reload();
    }

    /**
     * Creates a new JSON configuration file using the file that may or may not exist
     *
     * @param file the file to use as a JSON config, preferably suffixed with {@code .json}
     */
    public JsonConfig(File file) {
        this.path = file.toPath();
        this.reload();
    }

    @Override
    public void save() {
        try {
            Files.write(this.path,
                    GsonFactory.getGson().toJson(this.jsonHandle).getBytes(Charsets.UTF_8),
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public JsonConfig getRootSection() {
        return this;
    }

    @Override
    public JsonConfig getParentSection() {
        return this;
    }

    /**
     * Reloads the configuration
     */
    public void reload() {
        try {
            this.jsonHandle = Files.isReadable(this.path) ?
                    new JsonParser().parse(Files.newBufferedReader(this.path, Charsets.UTF_8))
                            .getAsJsonObject() :
                    new JsonObject();
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            //TODO: Handle
        }
    }
}
