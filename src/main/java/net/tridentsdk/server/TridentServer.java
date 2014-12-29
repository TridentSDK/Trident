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

import com.google.common.collect.Sets;
import net.tridentsdk.*;
import net.tridentsdk.config.JsonConfig;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.event.EventHandler;
import net.tridentsdk.plugin.TridentPluginHandler;
import net.tridentsdk.server.netty.protocol.Protocol;
import net.tridentsdk.server.packets.play.out.PacketPlayOutPluginMessage;
import net.tridentsdk.server.player.OfflinePlayer;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.threads.MainThread;
import net.tridentsdk.server.threads.ThreadsHandler;
import net.tridentsdk.server.window.WindowHandler;
import net.tridentsdk.server.world.RegionFileCache;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.server.world.TridentWorldLoader;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.window.Window;
import net.tridentsdk.world.World;
import org.slf4j.Logger;

import javax.annotation.concurrent.ThreadSafe;
import java.net.InetAddress;
import java.util.Set;
import java.util.UUID;

/**
 * The access base to internal workings of the server
 *
 * @author The TridentSDK Team
 */
@ThreadSafe public final class TridentServer implements Server {
    public static final TridentWorld WORLD = (TridentWorld) new TridentWorldLoader().load("world");
    private static final DisplayInfo INFO = new DisplayInfo();

    private final MainThread mainThread;

    private final JsonConfig config;
    private final Protocol protocol;
    private final Logger logger;

    private final RegionFileCache regionCache;

    private final WindowHandler windowHandler;
    private final EventHandler eventHandler;

    private final TridentPluginHandler pluginHandler;
    private final TridentScheduler scheduler;

    private final TridentWorldLoader worldLoader;

    private TridentServer(JsonConfig config) {
        this.config = config;
        this.protocol = new Protocol();
        this.regionCache = new RegionFileCache();
        this.windowHandler = new WindowHandler();
        this.eventHandler = EventHandler.create();
        this.pluginHandler = new TridentPluginHandler();
        this.scheduler = TridentScheduler.create();
        this.logger = TridentLogger.getLogger();
        this.mainThread = new MainThread(20);
        this.worldLoader = new TridentWorldLoader();
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

        return server;
        // We CANNOT let the "this" instance escape during creation, else we lose thread-safety
    }

    /**
     * Gets the instance of the server
     *
     * @return the server singleton
     */
    public static TridentServer getInstance() {
        return (TridentServer) Trident.getServer();
    }

    /**
     * Get the protocol base of the server
     *
     * @return the access to server protocol
     */
    public Protocol getProtocol() {
        return this.protocol;
    }

    public RegionFileCache getRegionFileCache() {
        return this.regionCache;
    }

    public int getCompressionThreshold() {
        return this.config.getInt("compression-threshold", Defaults.COMPRESSION_THRESHHOLD);
    }

    /**
     * Gets the port the server currently runs on
     *
     * @return the port occupied by the server
     */
    @Override
    public int getPort() {
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
        TridentLogger.log("Shutting down server connections...");
        TridentStart.close();
        TridentLogger.log("Shutting down worker threads...");
        this.scheduler.stop();
        TridentLogger.log("Shutting down server process...");
        ThreadsHandler.stopAll();
        TridentLogger.log("Server shutdown successfully.");
    }

    @Override
    public Set<World> getWorlds() {
        Set<World> worlds = Sets.newHashSet();
        worlds.addAll(worldLoader.getWorlds());

        return worlds;
    }

    @Override
    public InetAddress getServerIp() {
        return null;
    }

    @Override
    public String getVersion() {
        // TODO: Make this more eloquent
        return "1.0-SNAPSHOT";
    }

    @Override
    public Difficulty getDifficulty() {
        byte difficulty = this.config().getByte("difficulty", Defaults.DIFFICULTY.asByte());
        switch (difficulty) {
            case 0:
                return Difficulty.PEACEFUL;
            case 1:
                return Difficulty.EASY;
            case 2:
                return Difficulty.NORMAL;
            case 3:
                return Difficulty.HARD;
        }
        return null;
    }

    @Override
    public Window getWindow(int id) {
        return this.windowHandler.getWindow(id);
    }

    @Override
    public EventHandler getEventHandler() {
        return this.eventHandler;
    }

    @Override
    public void sendPluginMessage(String channel, byte... data) {
        TridentPlayer.sendAll(new PacketPlayOutPluginMessage().set("channel", channel).set("data", data));
    }

    @Override
    public TridentPluginHandler getPluginHandler() {
        return this.pluginHandler;
    }

    @Override
    public DisplayInfo getInfo() {
        return INFO;
    }

    @Override
    public Player getPlayer(UUID id) {
        Player p = TridentPlayer.getPlayer(id);
        return p != null ? p : OfflinePlayer.getOfflinePlayer(id);
    }
}
