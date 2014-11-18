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
package net.tridentsdk.api;

import com.google.common.base.Preconditions;
import net.tridentsdk.api.config.JsonConfig;
import net.tridentsdk.api.event.EventManager;
import net.tridentsdk.api.plugin.TridentPluginHandler;
import net.tridentsdk.api.scheduling.Scheduler;
import net.tridentsdk.api.threads.ThreadProvider;
import net.tridentsdk.api.util.TridentLogger;
import net.tridentsdk.api.window.Window;
import net.tridentsdk.api.world.World;

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
     * Gets the impl singleton that is currently running
     *
     * @return the impl that is running
     */
    public static Server getServer() {
        return server;
    }

    /**
     * Do not call <p/> <p>Will throw an exception if you are not calling from a trusted source</p>
     *
     * @param s the impl to set
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
     * Gets the logger which the impl is currently using
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
