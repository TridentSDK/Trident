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
import net.tridentsdk.command.CommandHandler;
import net.tridentsdk.command.CommandSourceType;
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
import net.tridentsdk.world.World;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
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
    private final CommandHandler commandHandler = new CommandHandler();
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
     *
     * @param config the server config
     * @param console the console to log to
     * @param net the network connection
     * @param list the list of server operators
     * @return the Trident server
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
    public TridentPlayer getPlayer(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid cannot be null");
        TridentPlayer player = TridentPlayer.getPlayers().get(uuid);
        return player != null && player.net().getState() == NetClient.NetState.PLAY ? player : null;
    }

    @Override
    public TridentPlayer getPlayerExact(String name) {
        Objects.requireNonNull(name, "name cannot be null");
        TridentPlayer player = TridentPlayer.getPlayerNames().get(name);
        return player != null && player.net().getState() == NetClient.NetState.PLAY ? player : null;
    }

    @Override
    public Collection<TridentPlayer> getPlayersMatching(String name) {
        Objects.requireNonNull(name, "name cannot be null");
        return getPlayers().stream()
                .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<TridentPlayer> getPlayersFuzzyMatching(String filter) {
        Objects.requireNonNull(filter, "filter cannot be null");
        return getPlayers().stream()
                .filter(p -> {
                    String f = filter;
                    String n = p.getName();
                    while (n.length() >= f.length()) {
                        if (f.length() == 0 || n.length() == 0)
                            return true;
                        int index = n.indexOf(f.charAt(0));
                        if (index < 0)
                            break;
                        n = n.substring(index + 1);
                        f = f.substring(1);
                    }
                    return false;
                })
                .collect(Collectors.toList());
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
    public CommandHandler getCommandHandler() {
        return this.commandHandler;
    }

    @Override
    @Policy("call only from plugin thread")
    public void reload() {
        Debug.tryCheckThread();
        this.logger.warn("SERVER RELOADING...");

        try {
            this.logger.log("Reloading server configs...");
            this.config.save();
            this.opsList.save();
            this.config.load();
            this.opsList.load();
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
            this.logger.log("Unloading plugins...");
            if (!this.pluginLoader.unloadAll()) {
                this.logger.error("Unloading plugins failed...");
            }

            this.tick.interrupt();
            this.logger.log("Kicking players... ");
            int removed = 0;
            Semaphore sem = new Semaphore(0);
            for (TridentPlayer player : TridentPlayer.getPlayers().values()) {
                removed++;
                player.net().disconnect(ChatComponent.text("Server closed")).addListener(future -> sem.release());
            }
            sem.tryAcquire(removed, 10, TimeUnit.SECONDS);
            this.logger.log("Closing network connections...");
            this.server.shutdown();
            for (World world : TridentWorldLoader.getInstance().getWorlds().values()) {
                this.logger.log("Saving world \"" + world.getName() + "\"...");
                world.save();
            }
            this.logger.log("Saving server config...");
            this.config.save();
            this.logger.log("Shutting down server process...");
            ServerThreadPool.shutdownAll();
        } catch (IOException | InterruptedException e) {
            JiraExceptionCatcher.serverException(e);
            return;
        }

        this.logger.success("Server has shutdown successfully.");
        System.exit(0);
    }

    /* CommandSource methods */

    @Override
    public void runCommand(String command) {
        this.logger.log("Server command issued by console: /" + command);
        try {
            if (!ServerThreadPool.forSpec(PoolSpec.PLUGINS).submit(() -> this.commandHandler.dispatch(command, this)).get()) {
                this.logger.log("No command \"" + command.split(" ")[0] + "\" found");
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessage(ChatComponent text) {
        StringBuilder builder = new StringBuilder();
        if (text.getColor() != null)
            builder.append(text.getColor());
        builder.append(text.getText());
        for (ChatComponent e : text.getExtra()) {
            if (e.getColor() != null) {
                builder.append(e.getColor());
            }
            builder.append(e.getText());
        }

        this.logger.log(builder.toString());
    }

    @Override
    public CommandSourceType getCmdType() { return CommandSourceType.CONSOLE; }

    @Override
    public boolean hasPermission(String permission) { return true; }

    @Override
    public void addPermission(String perm) {}

    @Override
    public boolean removePermission(String perm) { return false; }

    @Override
    public void setOp(boolean op) {}

    @Override
    public boolean isOp() { return true; }
}
