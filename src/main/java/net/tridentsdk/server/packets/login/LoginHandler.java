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

import io.netty.util.internal.chmv8.ConcurrentHashMapV8;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * Class used to store login usernames during the login stage
 */
public final class LoginHandler {
    /**
     * Instance of the class
     */
    protected static final LoginHandler instance = new LoginHandler();

    /**
     * Map used to store usernames with the address as the key
     */
    protected final Map<InetSocketAddress, String> loginNames = new ConcurrentHashMapV8<>();

    protected LoginHandler() {
    }

    public static LoginHandler getInstance() {
        return instance;
    }

    public void initLogin(InetSocketAddress address, String name) {
        this.loginNames.put(address, name);
    }

    public String name(InetSocketAddress address) {
        return this.loginNames.get(address);
    }

    public void finish(InetSocketAddress address) {
        this.loginNames.remove(address);
    }
}
