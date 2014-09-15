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

package net.tridentsdk.server.threads;

import net.tridentsdk.api.Location;
import net.tridentsdk.api.entity.*;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.util.Vector;
import net.tridentsdk.api.world.World;
import net.tridentsdk.server.netty.client.ClientConnection;

import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.util.concurrent.*;

/**
 * Player handling thread manager
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public final class PlayerThreads {
    static final ConcurrentTaskExecutor<ThreadPlayerWrapper> THREAD_MAP = new ConcurrentTaskExecutor<>(4);
    static final ConcurrentCache<ClientConnection, ThreadPlayerWrapper> CACHE_MAP = new ConcurrentCache<>();

    static final ExecutorService SERVICE = Executors.newSingleThreadExecutor();

    private PlayerThreads() {}

    /**
     * Gets the management tool for the player <p/> <p>This will put in a new value for the caches if cannot find for a
     * new player</p> <p/> <p>May block the first call</p>
     *
     * @param connection the player to find the wrapper for
     */
    public static Player clientThreadHandle(final ClientConnection connection) {
        return PlayerThreads.CACHE_MAP.retrieve(connection, new Callable<ThreadPlayerWrapper>() {
            @Override public ThreadPlayerWrapper call() throws Exception {
                ConcurrentTaskExecutor.TaskExecutor executor = PlayerThreads.THREAD_MAP.getScaledThread();
                ThreadPlayerWrapper wrapper = new ThreadPlayerWrapper(executor, connection);
                PlayerThreads.THREAD_MAP.assign(executor, wrapper);

                return wrapper;
            }
        }, PlayerThreads.SERVICE);
    }

    /**
     * Decaches the player connection handler from the mappings
     *
     * @param connection the player to remove the wrapper cache
     */
    public static void remove(ClientConnection connection) {
        PlayerThreads.THREAD_MAP.removeAssignment(PlayerThreads.CACHE_MAP.retrieve(connection));
    }

    /**
     * Gets all of the thread player wrappers
     *
     * @return the values of the concurrent cache
     */
    public static Collection<ThreadPlayerWrapper> wrappedPlayers() {
        return PlayerThreads.CACHE_MAP.values();
    }

    private static class ThreadPlayerWrapper implements Player {
        private final ConcurrentTaskExecutor.TaskExecutor executor;
        private final ClientConnection connection;

        /**
         * Wraps the thread player handling thread
         *
         * @param executor the handling thread to delegate actions to
         */
        ThreadPlayerWrapper(ConcurrentTaskExecutor.TaskExecutor executor, ClientConnection connection) {
            this.executor = executor;
            this.connection = connection;
        }

        public ClientConnection getConnection() {
            return this.connection;
        }

        public ConcurrentTaskExecutor.TaskExecutor getExecutor() {
            return this.executor;
        }

        // TODO
        @Override public void teleport(double x, double y, double z) {

        }

        @Override public void teleport(Entity entity) {

        }

        @Override public void teleport(Location location) {

        }

        @Override public World getWorld() {
            return null;
        }

        @Override public Location getLocation() {
            return null;
        }

        @Override public Vector getVelocity() {
            return null;
        }

        @Override public void setVelocity(Vector vector) {

        }

        @Override public void tick() {

        }

        @Override public boolean isOnGround() {
            return false;
        }

        @Override public List<Entity> getNearbyEntities(double radius) {
            return null;
        }



        @Override public void remove() {

        }

        @Override public Entity getPassenger() {
            return null;
        }

        @Override public void setPassenger(Entity entity) {

        }

        @Override public void eject() {

        }

        @Override public EntityType getType() {
            return null;
        }

        @Override
        public void applyProperties(EntityProperties properties) {

        }

        @Override
        public void hide(Entity entity) {

        }

        @Override
        public void show(Entity entity) {

        }

        @Override
        public double getHealth() {
            return 0;
        }

        @Override
        public double getMaxHealth() {
            return 0;
        }

        @Override
        public long getRemainingAir() {
            return 0;
        }

        @Override
        public String getDisplayName() {
            return null;
        }

        @Override
        public Location getEyeLocation() {
            return null;
        }

        @Override
        public boolean canPickupItems() {
            return false;
        }

        @Override
        public void setRemainingAir(long ticks) {

        }

        @Override
        public void setHealth(double health) {

        }

        @Override
        public void setMaxHealth(double maxHealth) {

        }

        @Override
        public boolean isDead() {
            return false;
        }

        @Override
        public int getId() {
            return 0;
        }
    }
}
