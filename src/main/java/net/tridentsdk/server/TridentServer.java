/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.server;

import net.tridentsdk.Defaults;
import net.tridentsdk.api.Difficulty;
import net.tridentsdk.api.Server;
import net.tridentsdk.api.Trident;
import net.tridentsdk.api.config.JsonConfig;
import net.tridentsdk.api.world.World;
import net.tridentsdk.entity.EntityManager;
import net.tridentsdk.server.netty.protocol.Protocol;
import net.tridentsdk.server.threads.ConcurrentTaskExecutor;
import net.tridentsdk.server.threads.ThreadsManager;
import net.tridentsdk.world.RegionFileCache;

import javax.annotation.concurrent.ThreadSafe;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/**
 * The access base to internal workings of the server
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public final class TridentServer implements Server {
    private final AtomicReference<Thread> SERVER_THREAD = new AtomicReference<>();

    private final JsonConfig config;
    private final Protocol protocol;
    private final ConcurrentTaskExecutor<?> taskExecutor;
    private final EntityManager entityManager;
    private final RegionFileCache regionCache;

    private TridentServer(JsonConfig config, ConcurrentTaskExecutor<?> taskExecutor) {
        this.config = config;
        this.protocol = new Protocol();
        this.taskExecutor = taskExecutor;
        this.entityManager = new EntityManager();
        this.regionCache = new RegionFileCache();
    }

    /**
     * Creates the server access base, distributing information to the fields available
     *
     * @param config the configuration to use for option lookup
     */
    public static TridentServer createServer(JsonConfig config, ConcurrentTaskExecutor<?> taskExecutor) {
        TridentServer server = new TridentServer(config, taskExecutor);
        Trident.setServer(server);

        server.SERVER_THREAD.set(server.taskExecutor.getScaledThread().asThread());

        return server;
        // We CANNOT let the "this" instance escape during creation, else we lose thread-safety
    }

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
        this.taskExecutor.getScaledThread().addTask(task);
    }

    @Override
    public Logger getLogger() {
        return null;
    }

    public JsonConfig getConfig() {
        return this.config;
    }

    /**
     * Performs the shutdown procedure on the server, ending with the exit of the JVM
     */
    @Override
    public void shutdown() {
        //TODO: Cleanup stuff...
        TridentStart.close();
        this.taskExecutor.shutdown();
        ThreadsManager.stopAll();
    }

    @Override
    public List<World> getWorlds() {
        return null;
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

    public void setMotd(String motd) {
        this.getConfig().setString("motd", motd);
    }

    @Override
    public File getMotdPicture() {
        return null;
    }
}
