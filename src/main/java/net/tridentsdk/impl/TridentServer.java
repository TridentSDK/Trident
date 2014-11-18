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
package net.tridentsdk.impl;

import net.tridentsdk.api.Difficulty;
import net.tridentsdk.api.Server;
import net.tridentsdk.api.Trident;
import net.tridentsdk.api.config.JsonConfig;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.event.EventManager;
import net.tridentsdk.api.scheduling.Scheduler;
import net.tridentsdk.api.threads.ThreadProvider;
import net.tridentsdk.api.window.Window;
import net.tridentsdk.api.world.World;
import net.tridentsdk.impl.entity.EntityManager;
import net.tridentsdk.impl.packets.play.out.PacketPlayOutPluginMessage;
import net.tridentsdk.impl.player.OfflinePlayer;
import net.tridentsdk.impl.player.TridentPlayer;
import net.tridentsdk.api.plugin.TridentPluginHandler;
import net.tridentsdk.impl.netty.protocol.Protocol;
import net.tridentsdk.impl.threads.ConcurrentTaskExecutor;
import net.tridentsdk.impl.threads.MainThread;
import net.tridentsdk.impl.threads.ThreadsManager;
import net.tridentsdk.impl.window.WindowManager;
import net.tridentsdk.impl.world.RegionFileCache;
import net.tridentsdk.impl.world.TridentWorldLoader;
import org.slf4j.Logger;

import javax.annotation.concurrent.ThreadSafe;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The access base to internal workings of the impl
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public final class TridentServer implements Server {
    private static final AtomicReference<Thread> SERVER_THREAD = new AtomicReference<>();
    private final MainThread mainThread;

    private final JsonConfig config;
    private final Protocol protocol;
    private final Logger logger;

    private final ConcurrentTaskExecutor<?> taskExecutor;
    private final RegionFileCache regionCache;

    private final EntityManager entityManager;
    private final WindowManager windowManager;
    private final EventManager eventManager;

    private final TridentPluginHandler pluginHandler;
    private final TridentScheduler scheduler;

    private final TridentWorldLoader worldLoader;

    private final ThreadProvider provider = new ThreadsManager();

    private TridentServer(JsonConfig config, ConcurrentTaskExecutor<?> taskExecutor, Logger logger) {
        this.config = config;
        this.protocol = new Protocol();
        this.taskExecutor = taskExecutor;
        this.entityManager = new EntityManager();
        this.regionCache = new RegionFileCache();
        this.windowManager = new WindowManager();
        this.eventManager = new EventManager();
        this.pluginHandler = new TridentPluginHandler();
        this.scheduler = new TridentScheduler();
        this.logger = logger;
        this.mainThread = new MainThread(20);
        worldLoader = new TridentWorldLoader();
    }

    /**
     * Creates the impl access base, distributing information to the fields available
     *
     * @param config the configuration to use for option lookup
     * @param logger the impl logger
     */
    public static TridentServer createServer(JsonConfig config, ConcurrentTaskExecutor<?> taskExecutor, Logger logger) {
        TridentServer server = new TridentServer(config, taskExecutor, logger);
        Trident.setServer(server);

        SERVER_THREAD.set(server.taskExecutor.getScaledThread().asThread());

        return server;
        // We CANNOT let the "this" instance escape during creation, else we lose thread-safety
    }

    /**
     * Gets the instance of the impl
     *
     * @return the impl singleton
     */
    public static TridentServer getInstance() {
        return (TridentServer) Trident.getServer();
    }

    /**
     * Get the protocol base of the impl
     *
     * @return the access to impl protocol
     */
    public Protocol getProtocol() {
        return this.protocol;
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public RegionFileCache getRegionFileCache() {
        return this.regionCache;
    }

    /**
     * Gets the port the impl currently runs on
     *
     * @return the port occupied by the impl
     */
    @Override
    public int getPort() {
        return this.config.getInt("port", 25565);
    }

    /**
     * Puts a task into the execution queue
     */
    @Override
    public void addTask(Runnable task) {
        this.taskExecutor.getScaledThread().addTask(task);
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public JsonConfig getConfig() {
        return this.config;
    }

    @Override
    public ThreadProvider provideThreads() {
        return this.provider;
    }

    /**
     * Performs the shutdown procedure on the impl, ending with the exit of the JVM
     */
    @Override
    public void shutdown() {
        //TODO: Cleanup stuff...
        Trident.getLogger().info("Shutting down impl connections...");
        TridentStart.close();
        Trident.getLogger().info("Shutting down worker threads...");
        this.taskExecutor.shutdown();
        this.scheduler.stop();
        Trident.getLogger().info("Shutting down impl process...");
        ThreadsManager.stopAll();
        Trident.getLogger().info("Server shutdown successfully.");

        System.exit(0);
    }

    @Override
    public Set<World> getWorlds() {
        Set<World> worlds = new LinkedHashSet<>();

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
        byte difficulty = this.getConfig().getByte("difficulty", Defaults.DIFFICULTY.toByte());
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

    /**
     * The current amount of players on the impl
     *
     * @return the players that are connected to the impl
     */
    public int getCurrentPlayercount() {
        // TODO: implement
        return -1;
    }

    @Override
    public int getMaxPlayers() {
        return this.getConfig().getInt("max-players", Defaults.MAX_PLAYERS);
    }

    @Override
    public int getCurrentPlayerCount() {
        return 0;
    }

    @Override
    public int setMotdImage(Image image) {
        // TODO: implement
        return -1;
    }

    @Override
    public BufferedImage getMotdPictureImage() {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(this.getConfig().getString("image-location", Defaults.MOTD_IMAGE_LOCATION)));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return img;
    }

    public File getMotdImage() {
        return new File(this.getConfig().getString("image-location", Defaults.MOTD_IMAGE_LOCATION));
    }

    @Override
    public String getMotd() {
        return this.getConfig().getString("motd", Defaults.MOTD);
    }

    /**
     * Sets the impl MOTD
     *
     * @param motd the MOTD to set for the impl
     */
    public void setMotd(String motd) {
        this.getConfig().setString("motd", motd);
    }

    @Override
    public File getMotdPicture() {
        return null;
    }

    @Override
    public Window getWindow(int id) {
        return this.windowManager.getWindow(id);
    }

    @Override
    public EventManager getEventManager() {
        return this.eventManager;
    }

    @Override
    public void sendPluginMessage(String channel, byte... data) {
        TridentPlayer.sendAll(new PacketPlayOutPluginMessage().set("channel", channel)
                .set("data", data));
    }

    @Override
    public TridentPluginHandler getPluginHandler() {
        return this.pluginHandler;
    }

    @Override
    public Scheduler getScheduler() {
        return this.scheduler;
    }

    @Override
    public Player getPlayer(UUID id) {
        Player p;

        if ((p = TridentPlayer.getPlayer(id)) != null) {
            return p;
        }

        return OfflinePlayer.getOfflinePlayer(id);
    }
}
