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

import com.google.gson.*;

import javax.annotation.concurrent.Immutable;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This class is a config writer that centralizes logic
 * regarding output from memory to file
 */
@Immutable
public final class ConfigIo {
    /**
     * Gson object using readability settings
     */
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapterFactory(TridentAdapter.FACTORY)
            .serializeNulls()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();
    /**
     * JsonParser for straight reading the configs
     */
    private static final JsonParser PARSER = new JsonParser();

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

    /**
     * Reads the config file located at the given path into
     * memory.
     *
     * @param path the config file location
     * @return the in-memory representation of the config
     */
    public static JsonObject readConfig(Path path) {
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
        return PARSER.parse(json).getAsJsonObject();
    }

    /**
     * Writes the memory configuration object to the config
     * located at the given path.
     *
     * @param path   the config to write
     * @param object the memory representation of the config
     */
    public static void writeConfig(Path path, JsonObject object) {
        String json = GSON.toJson(object);

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

    /**
     * Converts the element to an object
     *
     * @param element the element to convert
     * @param cls     the type to which this method will convert
     * @param <T>     the type
     * @return the object
     */
    public static <T> T asObj(JsonElement element, Class<T> cls) {
        return GSON.fromJson(element, (Type) cls);
    }

    /**
     * Converts the object to a json object
     *
     * @param o the object to convert
     * @return the json object
     */
    public static JsonElement asJson(Object o) {
        return GSON.toJsonTree(o);
    }
}