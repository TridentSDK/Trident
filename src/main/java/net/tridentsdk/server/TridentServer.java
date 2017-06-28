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
import net.tridentsdk.command.CmdHandler;
import net.tridentsdk.command.CmdSourceType;
import net.tridentsdk.doc.Policy;
import net.tridentsdk.event.EventController;
import net.tridentsdk.logger.Logger;
import net.tridentsdk.plugin.PluginLoader;
import net.tridentsdk.server.concurrent.PoolSpec;
import net.tridentsdk.server.concurrent.ServerThreadPool;
import net.tridentsdk.server.concurrent.TridentTick;
import net.tridentsdk.server.config.OpsList;
import net.tridentsdk.server.config.ServerConfig;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.net.NetServer;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.plugin.TridentEventController;
import net.tridentsdk.server.util.Debug;
import net.tridentsdk.server.util.JiraExceptionCatcher;
import net.tridentsdk.server.world.TridentWorldLoader;
import net.tridentsdk.ui.chat.ChatComponent;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
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
     * The server operators list
     */
    @Getter
    private final OpsList opsList;
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
     * Singleton instance of the server plugin loader
     */
    private final PluginLoader pluginLoader = new PluginLoader();
    /**
     * Singleton instance of the server command handler
     */
    private final CmdHandler cmdHandler = new CmdHandler();
    /**
     * Whether or not the server is shutting down
     */
    @Getter
    private boolean shutdownState;

    /**
     * Creates a new server instance
     *
     * @param config the config to initialize the server
     * @param console the logger to which the server logs
     */
    private TridentServer(ServerConfig config,
                          Logger console,
                          NetServer server,
                          OpsList opsList) {
        this.config = config;
        this.logger = console;
        this.server = server;
        this.opsList = opsList;
        this.tick = new TridentTick(console);
    }

    /**
     * Init code for server startup
     */
    public static TridentServer init(ServerConfig config, Logger console,
                                     NetServer net, OpsList list) throws IllegalStateException {
        TridentServer server = new TridentServer(config, console, net, list);
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
    public Collection<UUID> getOps() {
        return Collections.unmodifiableCollection(this.opsList.getOps());
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
    public PluginLoader getPluginLoader() {
        return this.pluginLoader;
    }

    @Override
    public CmdHandler getCmdHandler() {
        return this.cmdHandler;
    }

    @Override
    @Policy("call only from plugin thread")
    public void reload() {
        Debug.tryCheckThread();
        this.logger.warn("SERVER RELOADING...");

        try {
            this.logger.log("Reloading server config...");
            this.config.save();
            this.logger.log("Reloading plugins...");
            this.pluginLoader.reload();
        } catch (IOException e) {
            JiraExceptionCatcher.serverException(e);
            return;
        }

        this.logger.success("Server has reloaded successfully.");
    }

    @Override
    @Policy("call only from plugin thread")
    public void shutdown() {
        Debug.tryCheckThread();
        this.logger.warn("SERVER SHUTTING DOWN...");
        this.shutdownState = true;
        try {
            this.logger.log("Kicking players...");
            TridentPlayer.getPlayers().values().forEach(p -> p.kick(ChatComponent.text("Server closed")));
            this.logger.log("Unloading plugins...");
            if (!this.pluginLoader.unloadAll()) {
                this.logger.error("Unloading plugins failed...");
            }
            this.logger.log("Saving server config...");
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
        System.exit(0);
    }

    @Override
    public void runCommand(String command) {
        this.logger.log("Server command issued by console: /" + command);
        try {
            if (!ServerThreadPool.forSpec(PoolSpec.PLUGINS).submit(() -> this.cmdHandler.dispatch(command, this)).get()) {
                this.logger.log("No command \"" + command.split(" ")[0] + "\" found");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(ChatComponent text) {
        StringBuilder builder = new StringBuilder();
        builder.append(text.getColor()).append(text.getText());
        for (ChatComponent e : text.getExtra()) {
            if (e.getColor() != null) {
                builder.append(e.getColor());
            }

            builder.append(e.getText());
        }

        this.logger.log(builder.toString());
    }

    @Override
    public CmdSourceType getCmdType() {
        return CmdSourceType.CONSOLE;
    }
}
