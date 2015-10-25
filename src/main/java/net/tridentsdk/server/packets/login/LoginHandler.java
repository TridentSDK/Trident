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

package net.tridentsdk.server.packets.login;


import com.google.common.collect.Maps;
import net.tridentsdk.Trident;
import net.tridentsdk.registry.Registered;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class used to store login usernames during the login stage
 */
public final class LoginHandler {
    private static final long THROTTLE_MS = 4000L;

    /**
     * Instance of the class
     */
    protected static final LoginHandler instance = new LoginHandler();

    /**
     * Map used to store usernames with the address as the key
     */
    protected final Map<InetSocketAddress, String> loginNames = Maps.newHashMap();
    private final Map<String, Long> times = new ConcurrentHashMap<>();

    protected LoginHandler() {
    }

    public static LoginHandler getInstance() {
        return instance;
    }

    public boolean initLogin(InetSocketAddress address, String name) {
        synchronized (this) {
            return loginNames.size() + Registered.players().size() < Trident.info().maxPlayers() &&
                    loginNames.put(address, name) == null && !throttled(name);
        }
    }

    public String name(InetSocketAddress address) {
        synchronized (this) {
            return this.loginNames.get(address);
        }
    }

    public void finish(InetSocketAddress address) {
        synchronized (this) {
            this.loginNames.remove(address);
        }
    }

    private boolean throttled(String name) {
        long time = System.currentTimeMillis();
        times.forEach((k, v) -> {
            if (time - v > THROTTLE_MS) {
                times.remove(k);
            }
        });

        boolean b = times.containsKey(name);
        if (!b) {
            times.put(name, System.currentTimeMillis());
        }

        return b;
    }
}
