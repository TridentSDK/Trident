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
package net.tridentsdk.server;

import lombok.Getter;
import net.tridentsdk.Server;
import net.tridentsdk.command.logger.Logger;
import net.tridentsdk.doc.Policy;
import net.tridentsdk.event.EventController;
import net.tridentsdk.server.concurrent.ServerThreadPool;
import net.tridentsdk.server.concurrent.TridentTick;
import net.tridentsdk.server.config.ServerConfig;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.net.NetServer;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.plugin.TridentEventController;
import net.tridentsdk.server.util.JiraExceptionCatcher;
import net.tridentsdk.server.world.TridentWorldLoader;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * This class represents the running Minecraft server
 */
@Policy("singleton")
@ThreadSafe
public class TridentServer implements Server {
    /**
     * The instance of the TridentServer, if it exists
     */
    @Getter
    private static volatile TridentServer instance;

    /**
     * The configuration file used by the server
     */
    @Getter
    private final ServerConfig config;
    /**
     * The logger to which the server logs
     */
    @Getter
    private final Logger logger;
    /**
     * The socket channel handler instance
     */
    private final NetServer server;
    /**
     * The ticking thread for the server
     */
    private final TridentTick tick;

    /**
     * Creates a new server instance
     *
     * @param config the config to initialize the server
     * @param console the logger to which the server logs
     */
    private TridentServer(ServerConfig config,
                          Logger console,
                          NetServer server) {
        this.config = config;
        this.logger = console;
        this.server = server;
        this.tick = new TridentTick(console);
    }

    /**
     * Init code for server startup
     */
    public static TridentServer init(ServerConfig config, Logger console,
                                     NetServer net) throws IllegalStateException {
        TridentServer server = new TridentServer(config, console, net);
        if (TridentServer.instance == null) {
            TridentServer.instance = server;
            server.tick.start();
            return server;
        }

        throw new IllegalStateException("Server is already initialized");
    }

    /**
     * Shortcut method to retrieving the server config.
     *
     * @return the server config
     */
    public static ServerConfig cfg() {
        return instance.getConfig();
    }

    @Override
    public String getIp() {
        return this.config.ip();
    }

    @Override
    public int getPort() {
        return this.config.port();
    }

    @Override
    public Collection<TridentPlayer> getPlayers() {
        return TridentPlayer.getPlayers().values()
                .stream()
                .filter(p -> p.net().getState() == NetClient.NetState.PLAY)
                .collect(Collectors.toSet());
    }

    @Override
    public TridentWorldLoader getWorldLoader() {
        return TridentWorldLoader.getInstance();
    }

    @Override
    public EventController getEventController() {
        return TridentEventController.getInstance();
    }

    @Override
    public void reload() {
        this.logger.warn("SERVER RELOADING...");

        try {
            this.logger.log("Saving config...");
            this.config.save();
        } catch (IOException e) {
            JiraExceptionCatcher.serverException(e);
            return;
        }

        this.logger.success("Server has reloaded successfully.");
    }

    @Override
    public void shutdown() {
        this.logger.warn("SERVER SHUTTING DOWN...");
        try {
            this.logger.log("Saving config...");
            this.config.save();
            this.logger.log("Shutting down server process...");
            this.tick.interrupt();
            ServerThreadPool.shutdownAll();
            this.logger.log("Closing network connections...");
            this.server.shutdown();
        } catch (IOException | InterruptedException e) {
            JiraExceptionCatcher.serverException(e);
            return;
        }

        this.logger.success("Server has shutdown successfully.");
    }
}
