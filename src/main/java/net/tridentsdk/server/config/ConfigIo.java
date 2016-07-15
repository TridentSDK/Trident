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
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This class is a config writer that centralizes logic
 * regarding output from memory to file
 */
public class ConfigIo {
    /**
     * Exports the given resource and copies it into the
     * given destination path.
     *
     * @param dest     the destination
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

    public static JsonObject readConfig(Path path) {
        // TODO handle not json?
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            FileInputStream stream = new FileInputStream(path.toFile());

            byte[] buffer = new byte[8192];
            while (stream.read(buffer, 0, buffer.length) > -1) {
                out.write(buffer, 0, buffer.length);
            }

            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String json = new String(out.toByteArray()).trim();
        JsonParser parser = new JsonParser();
        return parser.parse(json).getAsJsonObject();
    }

    public static void writeConfig(Path path, JsonObject object) {
        // TODO implement
    }
}