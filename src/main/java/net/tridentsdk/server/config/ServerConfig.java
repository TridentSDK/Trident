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

import net.tridentsdk.config.IoResponse;
import net.tridentsdk.util.Misc;

import java.nio.file.Paths;

/**
 * This class contains the constant values whenever the
 * server loads the properties file in order to shortcut
 * access to each of the values.
 */
public class ServerConfig extends TridentConfig {
    /**
     * The internal server address to which the socket
     * will be bound
     */
    private volatile String address;
    /**
     * The server port to use
     */
    private volatile int port;

    /**
     * Initializes the server file and load all the
     * predefined fields into memory
     */
    public ServerConfig() {
        super(Paths.get(Misc.HOME, "server.json"));
    }

    /**
     * Obtains the internal address to which the server will
     * bind the socket.
     * <p>
     * <p>By default, this needs to be
     * {@code 0.0.0.0}</p>
     *
     * @return the internal address
     */
    public String address() {
        return this.address;
    }

    /**
     * Obtains the port that the connection will use to
     * transport packets over the socket.
     *
     * @return the port
     */
    public int port() {
        return this.port;
    }

    @Override
    public IoResponse load() {
        IoResponse response = super.load();
        if (response == IoResponse.SUCCESS) {
            this.address = getString("address");
            this.port = getInt("port");
        }

        return response;
    }
}