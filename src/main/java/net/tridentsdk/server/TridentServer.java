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

package net.tridentsdk.server;

import com.google.common.collect.Maps;
import net.tridentsdk.Defaults;
import net.tridentsdk.DisplayInfo;
import net.tridentsdk.Server;
import net.tridentsdk.Trident;
import net.tridentsdk.concurrent.TridentRunnable;
import net.tridentsdk.config.JsonConfig;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.event.EventHandler;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.plugin.TridentPlugin;
import net.tridentsdk.plugin.TridentPluginHandler;
import net.tridentsdk.plugin.cmd.CommandHandler;
import net.tridentsdk.plugin.cmd.ServerConsole;
import net.tridentsdk.server.command.TridentConsole;
import net.tridentsdk.server.netty.protocol.Protocol;
import net.tridentsdk.server.packets.play.out.PacketPlayOutPluginMessage;
import net.tridentsdk.server.player.OfflinePlayer;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.threads.ConcurrentTaskExecutor;
import net.tridentsdk.server.threads.MainThread;
import net.tridentsdk.server.threads.ThreadsHandler;
import net.tridentsdk.server.window.WindowHandler;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.server.world.TridentWorldLoader;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.window.Window;
import net.tridentsdk.world.World;
import net.tridentsdk.world.WorldLoader;
import net.tridentsdk.world.gen.AbstractGenerator;
import org.slf4j.Logger;

import javax.annotation.concurrent.ThreadSafe;
import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;

/**
 * The access base to internal workings of the server
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public final class TridentServer implements Server {
    // TODO this is temporary for testing
    public static final TridentWorld WORLD = findWorld();
    private static final DisplayInfo INFO = new DisplayInfo();

    private final MainThread mainThread;

    private final JsonConfig config;
    private final Protocol protocol;
    private final Logger logger;

    private final WindowHandler windowHandler;
    private final EventHandler eventHandler;
    private final CommandHandler commandHandler;
    private final TridentConsole console;

    private final TridentPluginHandler pluginHandler;
    private final TridentWorldLoader worldLoader;

    private TridentServer(JsonConfig config) {
        this.config = config;
        this.protocol = new Protocol();
        this.windowHandler = new WindowHandler();
        this.eventHandler = EventHandler.create();
        this.commandHandler = new CommandHandler();
        this.pluginHandler = new TridentPluginHandler();
        this.logger = TridentLogger.logger();
        this.mainThread = new MainThread(20);
        this.worldLoader = new TridentWorldLoader();
        this.console = new TridentConsole();
    }

    /**
     * Creates the server access base, distributing information to the fields available
     *
     * @param config the configuration to use for option lookup
     */
    public static TridentServer createServer(JsonConfig config) {
        TridentServer server = new TridentServer(config);
        Trident.setServer(server);
        server.mainThread.start();
        server.worldLoader.loadAll();

        return server;
        // We CANNOT let the "this" instance escape during creation, else we lose thread-safety
    }

    private static TridentWorld findWorld() {
        final TridentWorld[] world = new TridentWorld[1];
        Factories.tasks().asyncLater(null, new TridentRunnable() {
            @Override
            public void run() {
                while (Trident.instance() == null) {
                }
                world[0] = (TridentWorld) Trident.worlds().get("world");
            }
        }, 100L);
        return world[0];
    }

    /**
     * Gets the instance of the server
     *
     * @return the server singleton
     */
    public static TridentServer instance() {
        return (TridentServer) Trident.instance();
    }

    /**
     * Get the protocol base of the server
     *
     * @return the access to server protocol
     */
    public Protocol getProtocol() {
        return this.protocol;
    }

    public int getCompressionThreshold() {
        return this.config.getInt("compression-threshold", Defaults.COMPRESSION_THRESHHOLD);
    }

    public int viewDistance() {
        return this.config.getInt("view-distance", Defaults.VIEW_DISTANCE);
    }

    public MainThread getMainThread() {
        return mainThread;
    }

    @Override
    public ServerConsole console() {
        return console;
    }

    /**
     * Gets the port the server currently runs on
     *
     * @return the port occupied by the server
     */
    @Override
    public int port() {
        return this.config.getInt("port", 25565);
    }

    @Override
    public JsonConfig config() {
        return this.config;
    }

    /**
     * Performs the shutdown procedure on the server, ending with the exit of the JVM
     */
    @Override
    public void shutdown() {
        //TODO: Cleanup stuff...
        TridentLogger.log("Shutting down plugins...");
        for (TridentPlugin plugin : pluginHandler().getPlugins())
            pluginHandler().disable(plugin);

        TridentLogger.log("Saving worlds...");

        for (World world : worldLoader.worlds())
            ((TridentWorld) world).save();

        TridentLogger.log("Shutting down worker threads...");
        ((TridentScheduler) Factories.tasks()).shutdown();

        TridentLogger.log("Shutting down server process...");
        ThreadsHandler.shutdownAll();

        TridentLogger.log("Shutting down thread pools...");
        for (ConcurrentTaskExecutor<?> executor : ConcurrentTaskExecutor.executors())
            executor.shutdown();

        TridentLogger.log("Shutting down server connections...");
        TridentStart.close();

        TridentLogger.log("Server shutdown successfully.");
    }

    @Override
    public Map<String, World> worlds() {
        Map<String, World> worlds = Maps.newHashMap();
        for (World world : worldLoader.worlds())
            worlds.put(world.name(), world);

        return worlds;
    }

    @Override
    public WorldLoader newWorldLoader(AbstractGenerator generator) {
        return new TridentWorldLoader(generator);
    }

    @Override
    public InetAddress serverIp() {
        return null;
    }

    @Override
    public String version() {
        // TODO: Make this more eloquent
        return "1.0-SNAPSHOT";
    }

    @Override
    public Window windowBy(int id) {
        return this.windowHandler.getWindow(id);
    }

    @Override
    public EventHandler eventHandler() {
        return this.eventHandler;
    }

    @Override
    public void sendPluginMessage(String channel, byte... data) {
        TridentPlayer.sendAll(new PacketPlayOutPluginMessage().set("channel", channel).set("data", data));
    }

    @Override
    public TridentPluginHandler pluginHandler() {
        return this.pluginHandler;
    }

    @Override
    public CommandHandler commandHandler() {
        return commandHandler;
    }

    @Override
    public DisplayInfo info() {
        return INFO;
    }

    @Override
    public Logger logger() {
        return logger;
    }

    @Override
    public Player playerBy(UUID id) {
        Player p = TridentPlayer.getPlayer(id);
        return p != null ? p : OfflinePlayer.getOfflinePlayer(id);
    }
}
