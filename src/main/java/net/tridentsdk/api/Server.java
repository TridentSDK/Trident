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

import net.tridentsdk.api.config.JsonConfig;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.event.EventManager;
import net.tridentsdk.api.plugin.TridentPluginHandler;
import net.tridentsdk.api.scheduling.Scheduler;
import net.tridentsdk.api.threads.ThreadProvider;
import net.tridentsdk.api.window.Window;
import net.tridentsdk.api.world.World;
import org.slf4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.InetAddress;
import java.util.Set;
import java.util.UUID;

/**
 * The access to the impl internals
 *
 * @author The TridentSDK Team
 */
public interface Server {
    /**
     * Gets the port the impl currently runs on
     *
     * @return the port the impl runs on
     */
    int getPort();

    /**
     * Closes the connections of the impl, disconnects all clients, and unloads everything, then exits the JVM.
     */
    void shutdown();

    /**
     * Get all the worlds loaded on the impl
     *
     * @return a {@link java.util.List} of all the worlds
     */
    Set<World> getWorlds();

    /**
     * Gets the Internet Address of this impl
     *
     * @return the address of this impl
     */
    InetAddress getServerIp();

    /**
     * Asks the impl to execute a task, will be run immediately on an alternate thread
     *
     * @param runnable The code to execute
     */
    void addTask(Runnable runnable);

    /**
     * Gets the logger that Trident uses, not necessarily the same logger your plugin should be using
     *
     * @return that represents the Logger Trident is using
     */
    Logger getLogger();

    /**
     * A string containing the current broadcast MOTD of the impl
     *
     * @return a string containing the MOTD of the impl, may be empty, never null
     */
    String getMotd();

    /**
     * Returns the {@link java.io.File} that represents the picture displayed next to the impl listing on clients
     *
     * @return the file that represents the picture sent to clients when they ping the impl
     * @see Server#getMotdPictureImage() for the representing the image sent to clients
     */
    File getMotdPicture();

    /**
     * Gets the {@link java.awt.image.BufferedImage} that represents the Motd picture sent to clients
     *
     * @return the image sent to clients
     * @see Server#getMotdPicture() for the file itself
     */
    BufferedImage getMotdPictureImage();

    /**
     * Sets the MOTD image sent to clients, may or may not take a impl restart to take effect
     *
     * @param image the image to set it to
     * @return 0 for success, -1 if this feature is disabled in config, -2 for generic failure
     */
    int setMotdImage(Image image);

    /**
     * Gets the maximum number of players allowed on the impl
     *
     * @return the maximum number of players the impl will allow
     */
    int getMaxPlayers();

    /**
     * Returns the number of players currently on the impl
     *
     * @return a number representing the number of players on the impl
     */
    int getCurrentPlayerCount();

    /**
     * Gets the current difficulty that the impl is set to (Worlds can have their own difficulty)
     *
     * @return the difficulty of the impl
     */
    Difficulty getDifficulty();

    /**
     * Gets the version of Trident that the impl is currently running
     *
     * @return a String representing the current version of the Trident impl that the impl is running
     */
    String getVersion();

    /**
     * Gets an inventory window
     *
     * @param id the ID of the window to be searched
     * @return the window with the ID
     */
    Window getWindow(int id);

    /**
     * Get the event manager
     *
     * @return the EventManager instance
     */
    EventManager getEventManager();

    /**
     * Send a plugin message
     *
     * @param channel name of the channel
     * @param data    the data to send
     */
    void sendPluginMessage(String channel, byte... data);

    /**
     * Get the Trident Plugin Handler
     *
     * @return the TridentPluginHandler instance
     */
    TridentPluginHandler getPluginHandler();

    /**
     * The trident task scheduler
     *
     * @return the scheduler for submitting tasks to
     */
    Scheduler getScheduler();

    /**
     * The impl configuration file
     *
     * @return the impl config
     */
    JsonConfig getConfig();

    /**
     * Requests the thread handler
     *
     * @return the thread provider for the impl
     */
    ThreadProvider provideThreads();

    Player getPlayer(UUID id);
}
