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
package net.tridentsdk.server;

import net.tridentsdk.Server;
import net.tridentsdk.command.logger.Logger;
import net.tridentsdk.server.config.ServerConfig;

import java.io.IOException;

/**
 * This class represents the running Minecraft server
 */
public class TridentServer implements Server {
    private static volatile TridentServer instance;

    /**
     * The configuration file used by the server
     */
    private final ServerConfig config;
    /**
     * The logger to which the server logs
     */
    private final Logger logger;

    /**
     * Creates a new server instance
     *
     * @param config the config to initialize the server
     * @param console the logger to which the server logs
     */
    private TridentServer(ServerConfig config, Logger console) {
        this.config = config;
        this.logger = console;
    }

    /**
     * Init code for server startup
     */
    public static TridentServer init(ServerConfig config, Logger console) throws IllegalStateException {
        TridentServer server = new TridentServer(config, console);
        if (TridentServer.instance == null) {
            TridentServer.instance = server;
            return server;
        }

        throw new IllegalStateException("Server is already initialized");
    }

    /**
     * Obtains the singleton instance of the server
     * implementation.
     *
     * @return instance of server
     */
    public static TridentServer instance() {
        return TridentServer.instance;
    }

    @Override
    public String ip() {
        return config.address();
    }

    @Override
    public int port() {
        return config.port();
    }

    @Override
    public Logger console() {
        return this.logger;
    }

    @Override
    public String version() {
        return "0.5-alpha";
    }

    // TODO lifecycle

    @Override
    public void reload() {
        logger.warn("SERVER RELOADING");

        try {
            logger.logp("Saving config...");
            config.save();
            logger.success("Saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.success("Server has reloaded successfully.");
    }

    @Override
    public void shutdown() {
    }
}