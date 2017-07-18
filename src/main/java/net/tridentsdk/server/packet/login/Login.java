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
package net.tridentsdk.server.packet.login;

import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.util.Cache;

import javax.annotation.concurrent.ThreadSafe;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * Login utilities.
 */
@ThreadSafe
public final class Login {
    /**
     * The amount of players that are queued to login
     */
    private static final AtomicInteger LOGGING_IN = new AtomicInteger();
    /**
     * Cache of UUIDs as Mojang doesn't like it when you
     * push the rate limit.
     */
    private static final Cache<String, UUID> UUID_CACHE = new Cache<>(1000 * 60 * 5); // 5 Minutes
    /**
     * The pattern used to add back dashes to the UUID
     */
    private static final Pattern UUID_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

    // Prevent instantiation
    private Login() {
    }

    /**
     * Ensures that the given network connection is able to
     * login to the server.
     *
     * @param client the client to test
     * @return {@code true} if the client may login
     */
    public static boolean canLogin(NetClient client) {
        if (LOGGING_IN.get() + TridentPlayer.getPlayers().size() >=
                TridentServer.cfg().maxPlayers()) {
            client.disconnect("Server is full");
            return false;
        }

        String name = client.getName();
        if (TridentPlayer.getPlayerNames().containsKey(name)) {
            client.disconnect("Player with name \"" + name + "\" already exists");
            return false;
        }

        LOGGING_IN.incrementAndGet();
        return true;
    }

    /**
     * Finishes a player login and removes a login spot.
     */
    public static void finish() {
        LOGGING_IN.decrementAndGet();
    }

    /**
     * Corrects input UUIDs that do not have dashes.
     *
     * @param name the name to cache
     * @param input the raw UUID from mojang
     * @return the dashed string
     */
    public static UUID convert(String name, String input) {
        return UUID_CACHE.get(name, () -> UUID.fromString(UUID_PATTERN.matcher(input).replaceAll("$1-$2-$3-$4-$5")));
    }
}
