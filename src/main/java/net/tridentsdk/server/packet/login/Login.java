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
package net.tridentsdk.server.packet.login;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Login utilities.
 */
public final class Login {
    /**
     * Cache of UUIDs as Mojang doesn't like it when you
     * push the rate limit.
     */
    private static final Cache<String, UUID> UUID_CACHE =
            CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();
    /**
     * Cached exception for escaping UUID lookup
     */
    private static final RuntimeException CACHED_EXCEPTION = new RuntimeException();
    /**
     * The pattern used to add back dashes to the UUID
     */
    private static final Pattern UUID_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

    // Prevent instantiation
    private Login() {
    }

    /**
     * Obtains the UUID for the given name by Mojang
     * lookup.
     *
     * @param name the name to find the UUID
     * @return the UUID
     */
    public static UUID uuidFor(String name) {
        try {
            return UUID_CACHE.get(name, () -> {
                JsonArray array = new JsonArray();
                array.add(new JsonPrimitive(name));
                UUID uuid = Mojang.<UUID>req("https://api.mojang.com/profiles/minecraft").callback(element -> {
                    return UUID.fromString(UUID_PATTERN.matcher(
                            element.getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString()
                    ).replaceAll("$1-$2-$3-$4-$5"));
                }).post(array).get();

                if (uuid == null) throw CACHED_EXCEPTION;
                return uuid;
            });
        } catch (ExecutionException e) {
            return null;
        }
    }

    /**
     * Corrects input UUIDs that do not have dashes.
     *
     * @param input the raw UUID from mojang
     * @return the dashed string
     */
    public static UUID convert(String name, String input) {
        try {
            return UUID_CACHE.get(name, () -> UUID.fromString(UUID_PATTERN.matcher(input).replaceAll("$1-$2-$3-$4-$5")));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}