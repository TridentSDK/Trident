/*
 * Copyright (C) 2014 The TridentSDK Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tridentsdk.server;

import net.tridentsdk.api.Server;
import net.tridentsdk.server.netty.protocol.Protocol;

/**
 * The access base to internal workings of the server
 *
 * @author The TridentSDK Team
 */
public class TridentServer implements Server, Runnable {
    private final TridentConfig   config;
    private final Protocol        protocol;
    private final Thread          serverThread;

    /**
     * Creates the server access base, distributing information to the fields available
     *
     * @param config the configuration to use for option lookup
     */
    protected TridentServer(TridentConfig config) {
        this.serverThread = Thread.currentThread();
        this.config = config;

        //TODO: Get protocol version from config... or elsewhere
        this.protocol = new Protocol();
    }

    /**
     * Get the protocol base of the server
     *
     * @return the access to server protocol
     */
    public Protocol getProtocol() {
        return this.protocol;
    }

    /**
     * Gets the port the server currently runs on
     *
     * @return the port occupied by the server
     */
    public int getPort() {
        return (int) this.config.getPort();
    }

    @Override
    public void run() {
        //TODO: Set some server stuff up
        //TODO: Main server Loop
    }

    /**
     * Performs the shutdown procedure on the server, ending with the exit of the JVM
     */
    @Override
    public void shutdown() {
        //TODO: Cleanup stuff...
        TridentStart.shutdown();
    }
}
