/*
 *     Trident - A Multithreaded Server Alternative
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.server;

import net.tridentsdk.Defaults;
import net.tridentsdk.api.Difficulty;
import net.tridentsdk.api.Server;
import net.tridentsdk.api.Trident;
import net.tridentsdk.api.config.JsonConfig;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.event.EventManager;
import net.tridentsdk.api.window.Window;
import net.tridentsdk.api.world.World;
import net.tridentsdk.entity.EntityManager;
import net.tridentsdk.packets.play.out.PacketPlayOutPluginMessage;
import net.tridentsdk.player.OfflinePlayer;
import net.tridentsdk.player.TridentPlayer;
import net.tridentsdk.plugin.TridentPluginHandler;
import net.tridentsdk.server.netty.protocol.Protocol;
import net.tridentsdk.server.threads.ConcurrentTaskExecutor;
import net.tridentsdk.server.threads.MainThread;
import net.tridentsdk.server.threads.ThreadsManager;
import net.tridentsdk.window.WindowManager;
import net.tridentsdk.world.RegionFileCache;
import net.tridentsdk.world.TridentWorldLoader;
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
 * The access base to internal workings of the server
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
     * Creates the server access base, distributing information to the fields available
     *
     * @param config the configuration to use for option lookup
     * @param logger the server logger
     */
    public static TridentServer createServer(JsonConfig config, ConcurrentTaskExecutor<?> taskExecutor, Logger logger) {
        TridentServer server = new TridentServer(config, taskExecutor, logger);
        Trident.setServer(server);

        SERVER_THREAD.set(server.taskExecutor.scaledThread().asThread());

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

    public EntityManager getEntityManager() {
        return this.entityManager;
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

    /**
     * Puts a task into the execution queue
     */
    @Override
    public void addTask(Runnable task) {
        this.taskExecutor.scaledThread().addTask(task);
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public JsonConfig getConfig() {
        return this.config;
    }

    /**
     * Performs the shutdown procedure on the server, ending with the exit of the JVM
     */
    @Override
    public void shutdown() {
        //TODO: Cleanup stuff...
        Trident.getLogger().info("Shutting down server connections...");
        TridentStart.close();
        Trident.getLogger().info("Shutting down worker threads...");
        this.taskExecutor.shutdown();
        this.scheduler.stop();
        Trident.getLogger().info("Shutting down server process...");
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
     * The current amount of players on the server
     *
     * @return the players that are connected to the server
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
     * Sets the server MOTD
     *
     * @param motd the MOTD to set for the server
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
    public Player getPlayer(UUID id) {
        Player p;

        if ((p = TridentPlayer.getPlayer(id)) != null) {
            return p;
        }

        return OfflinePlayer.getOfflinePlayer(id);
    }
}
