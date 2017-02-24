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

import java.nio.charset.StandardCharsets;
import javax.annotation.concurrent.Immutable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * This class is a config writer that centralizes logic
 * regarding output from memory to file
 */
@Immutable
public final class ConfigIo {

    // Prevent instantiation
    private ConfigIo() {
    }

    /**
     * Exports the given resource and copies it into the
     * given destination path.
     *
     * @param dest the destination
     * @param resource the resource to copy
     */
    public static void exportResource(Path dest, String resource) {
        InputStream stream = ConfigIo.class.getResourceAsStream(resource);
        try {
            Files.copy(stream, dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the config file located at the given path into
     * memory.
     *
     * @param path the config file location
     * @return the in-memory representation of the config
     */
    public static JSONObject readConfig(Path path) throws IOException {
        JSONObject object;
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JSONTokener tokener = new JSONTokener(reader);
            object = (JSONObject) tokener.nextValue();
        }
        return object;
    }

    /**
     * Writes the memory configuration object to the config
     * located at the given path.
     *
     * @param path the config to write
     * @param object the memory representation of the
     * config
     */
    public static void writeConfig(Path path, JSONObject object) throws IOException {
        String json = object.toString();

        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            FileOutputStream stream = new FileOutputStream(path.toFile());
            stream.write(json.getBytes());

            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
