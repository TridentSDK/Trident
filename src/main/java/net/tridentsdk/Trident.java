/*
 *     TridentSDK - A Minecraft Server API
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
package net.tridentsdk;

import com.google.common.base.Preconditions;
import net.tridentsdk.config.JsonConfig;
import net.tridentsdk.event.EventManager;
import net.tridentsdk.plugin.TridentPluginHandler;
import net.tridentsdk.scheduling.Scheduler;
import net.tridentsdk.threads.ThreadProvider;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.window.Window;
import net.tridentsdk.world.World;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.InetAddress;
import java.util.Set;

/**
 * Utility accessor to the {@link Server}
 *
 * @author The TridentSDK Team
 */
public final class Trident {
    private static Server server;
    private static TridentLogger logger;

    private Trident() {
    }

    /**
     * Gets the server singleton that is currently running
     *
     * @return the server that is running
     */
    public static Server getServer() {
        return server;
    }

    /**
     * Do not call <p/> <p>Will throw an exception if you are not calling from a trusted source</p>
     *
     * @param s the server to set
     */
    public static void setServer(Server s) {
        Preconditions.checkState(isTrident(), "Server instance can only be set by TridentSDK!");
        server = s;
    }

    public static boolean isTrident() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        StackTraceElement element = elements[3];

        return element.getClassName().startsWith("net.tridentsdk");
    }

    /**
     * Gets the logger which the server is currently using
     *
     * @return the logger which is being used
     */
    public static TridentLogger getLogger() {
        return logger;
    }

    /**
     * Sets the output console logger
     *
     * @param logger the logger to use
     */
    public static void setLogger(TridentLogger logger) {
        Trident.logger = logger;
    }

    public static int getPort() {
        return server.getPort();
    }

    public static void shutdown() {
        server.shutdown();
    }

    public static Set<World> getWorlds() {
        return server.getWorlds();
    }

    public static InetAddress getServerIp() {
        return server.getServerIp();
    }

    public static void addTask(Runnable runnable) {
        server.addTask(runnable);
    }

    public static String getMotd() {
        return server.getMotd();
    }

    public static File getMotdPicture() {
        return server.getMotdPicture();
    }

    public static BufferedImage getMotdPictureImage() {
        return server.getMotdPictureImage();
    }

    public static int setMotdImage(Image image) {
        return server.setMotdImage(image);
    }

    public static int getMaxPlayers() {
        return server.getMaxPlayers();
    }

    public static int getCurrentPlayerCount() {
        return server.getCurrentPlayerCount();
    }

    public static Difficulty getDifficulty() {
        return server.getDifficulty();
    }

    public static String getVersion() {
        return server.getVersion();
    }

    public static Window getWindow(int id) {
        return server.getWindow(id);
    }

    public static EventManager getEventManager() {
        return server.getEventManager();
    }

    public static void sendPluginMessage(String channel, byte... data) {
        server.sendPluginMessage(channel, data);
    }

    public static TridentPluginHandler getPluginHandler() {
        return server.getPluginHandler();
    }

    public static Scheduler getScheduler() {
        return server.getScheduler();
    }

    public static JsonConfig getConfig() {
        return server.getConfig();
    }

    public static ThreadProvider provideThreads() {
        return server.provideThreads();
    }
}
