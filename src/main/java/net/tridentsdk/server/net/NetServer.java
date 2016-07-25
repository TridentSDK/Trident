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
package net.tridentsdk.server.net;

import net.tridentsdk.server.config.ServerConfig;

/**
 * This class handles the network connections for the server
 * and manages the netty channels, packets, pipelines, etc.
 */
public abstract class NetServer {
    private final String ip;
    private final int port;

    NetServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public static NetServer init(ServerConfig config) throws InterruptedException {
        boolean nativeCompat = System.getProperty("os.name").toLowerCase().contains("linux");
        String ip = config.ip();
        int port = config.port();

        return nativeCompat && config.useNative() ?
                new NetEpollServer(ip, port) : new NetNioServer(ip, port);
    }

    public abstract void setup() throws InterruptedException;

    public abstract void shutdown() throws InterruptedException;

    public int port() {
        return port;
    }

    public String ip() {
        return ip;
    }
}