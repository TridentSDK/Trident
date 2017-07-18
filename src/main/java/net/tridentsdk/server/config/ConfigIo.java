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

import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.hjson.Stringify;

import javax.annotation.concurrent.Immutable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

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
    public static JsonObject readConfig(Path path) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (FileInputStream stream = new FileInputStream(path.toFile())) {
            byte[] buffer = new byte[8192];
            while (stream.read(buffer, 0, buffer.length) > -1) {
                out.write(buffer, 0, buffer.length);
            }
        }

        String json = new String(out.toByteArray()).trim();
        return JsonValue.readHjson(json).asObject();
    }

    /**
     * Writes the memory configuration object to the config
     * located at the given path.
     *
     * @param path the config to write
     * @param object the memory representation of the
     * config
     */
    public static void writeConfig(Path path, JsonObject object) throws IOException {
        String json = object.toString(Stringify.HJSON);

        try (FileOutputStream stream = new FileOutputStream(path.toFile())) {
            stream.write(json.getBytes());
        }
    }

    /**
     * Converts the element to an object
     *
     * @param element the element to convert
     * @return the object
     */
    public static Object asObj(JsonValue element) {
        switch (element.getType()) {
            case STRING:
                return element.asString();
            case NUMBER:
                return element.asInt();
            case OBJECT:
                throw new RuntimeException("This is a config section");
            case ARRAY:
                return element.asArray();
            case BOOLEAN:
                return element.asBoolean();
            case NULL:
                throw new RuntimeException("Element cannot be null");
        }

        throw new RuntimeException("Cannot parse " + element.getType());
    }

    /**
     * Converts the object to a json object
     *
     * @param o the object to convert
     * @return the json object
     */
    public static JsonValue asJson(Object o) {
        if (o instanceof Double) {
            return JsonValue.valueOf((Double) o);
        }

        if (o instanceof Float) {
            return JsonValue.valueOf((Float) o);
        }

        if (o instanceof Long) {
            return JsonValue.valueOf((Long) o);
        }

        if (o instanceof Integer) {
            return JsonValue.valueOf((Integer) o);
        }

        if (o instanceof String) {
            return JsonValue.valueOf((String) o);
        }

        if (o instanceof Boolean) {
            return JsonValue.valueOf((Boolean) o);
        }

        if (o instanceof JsonValue) {
            return (JsonValue) o;
        }

        throw new RuntimeException("Objects must be preformatted as JsonObjects: " + o.getClass());
    }
}