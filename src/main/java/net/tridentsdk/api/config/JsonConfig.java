/*
 *     Trident - A Multithreaded Server Alternative
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
