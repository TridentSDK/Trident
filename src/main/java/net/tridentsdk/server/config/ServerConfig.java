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

import net.tridentsdk.util.Misc;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class contains the constant values whenever the
 * server loads the properties file in order to shortcut
 * access to each of the values.
 */
public class ServerConfig extends TridentConfig {
    /**
     * The path to the server configuration file
     */
    public static final Path PATH = Paths.get(Misc.HOME, "server.json");

    /**
     * The internal server ip to which the socket
     * will be bound
     */
    private volatile String ip;
    /**
     * The server port to use
     */
    private volatile int port;
    /**
     * Whether or not to use native transport (epoll)
     */
    private volatile boolean useNative;

    /**
     * Initializes the server file and load all the
     * predefined fields into memory
     */
    public ServerConfig() {
        super(PATH);
    }

    /**
     * Init code for server startup
     */
    public static ServerConfig init() throws IOException {
        ServerConfig config = new ServerConfig();
        config.load();
        return config;
    }

    /**
     * Obtains the internal ip to which the server will
     * bind the socket.
     *
     * <p>By default, this needs to be {@code 0.0.0.0}</p>
     *
     * @return the internal ip
     */
    public String ip() {
        return this.ip;
    }

    /**
     * Obtains the port that the connection will use to
     * transport packets over the socket.
     *
     * <p>By default, this needs to be {@code 25565}</p>
     *
     * @return the port
     */
    public int port() {
        return this.port;
    }

    /**
     * Obtains whether or not the server will use native
     * transport sockets for handling connections on linux
     * machines.
     *
     * <p>By default, thi sneeds to be {@code true}</p>
     *
     * @return {@code true} to use native transport if
     * possible
     */
    public boolean useNative() {
        return this.useNative;
    }

    @Override
    public void load() throws IOException {
        super.load();
        this.ip = getString("address");
        this.port = getInt("port");
        this.useNative = getBoolean("use-native");
    }
}