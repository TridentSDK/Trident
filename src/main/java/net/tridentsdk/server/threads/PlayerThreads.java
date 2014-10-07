/*
 * Copyright (c) 2014, TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of TridentSDK nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.server.threads;

import net.tridentsdk.api.GameMode;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.entity.*;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.event.entity.EntityDamageEvent;
import net.tridentsdk.api.inventory.ItemStack;
import net.tridentsdk.entity.TridentEntity;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.Packet;

import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.util.concurrent.*;

/**
 * Player handling thread manager, 4 threads by default
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public final class PlayerThreads {
    static final ConcurrentTaskExecutor<ThreadPlayerWrapper> THREAD_MAP = new ConcurrentTaskExecutor<>(4);
    static final ConcurrentCache<ClientConnection, ThreadPlayerWrapper> CACHE_MAP = new ConcurrentCache<>();

    static final ExecutorService SERVICE = Executors.newSingleThreadExecutor();

    private PlayerThreads() {
    }

    /**
     * Gets the management tool for the player
     *
     * <p>This will put in a new value for the caches if cannot find for a new player</p>
     *
     * <p>May block the first call</p>
     *
     * @param connection the player to find the wrapper for
     */
    public static Player clientThreadHandle(final ClientConnection connection) {
        return PlayerThreads.CACHE_MAP.retrieve(connection, new Callable<ThreadPlayerWrapper>() {
            @Override
            public ThreadPlayerWrapper call() throws Exception {
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
        PlayerThreads.THREAD_MAP.removeAssignment(PlayerThreads.CACHE_MAP.retrieve(connection, null,
                PlayerThreads.SERVICE));
        PlayerThreads.CACHE_MAP.remove(connection);
    }

    public static void sendAll(Packet packet) {
        for (ThreadPlayerWrapper wrapper : PlayerThreads.wrappedPlayers())
            wrapper.getConnection().sendPacket(packet);
    }

    /**
     * Gets all of the thread player wrappers
     *
     * @return the values of the concurrent cache
     */
    public static Collection<ThreadPlayerWrapper> wrappedPlayers() {
        return PlayerThreads.CACHE_MAP.values();
    }

    private static class ThreadPlayerWrapper extends TridentEntity implements Player {
        private final ConcurrentTaskExecutor.TaskExecutor executor;
        private final ClientConnection connection;

        /**
         * Wraps the thread player handling thread
         *
         * @param executor the handling thread to delegate actions to
         */
        ThreadPlayerWrapper(ConcurrentTaskExecutor.TaskExecutor executor, ClientConnection connection) {
            super(UUID.randomUUID(), new Location(null, 0.0, 0.0, 0.0));
            this.executor = executor;
            this.connection = connection;
        }

        public ClientConnection getConnection() {
            return this.connection;
        }

        public ConcurrentTaskExecutor.TaskExecutor getExecutor() {
            return this.executor;
        }

        @Override
        public void hide(Entity entity) {
        }

        @Override
        public void show(Entity entity) {

        }

        @Override
        public double getHealth() {
            return 0.0;
        }

        @Override
        public void setHealth(double health) {

        }

        @Override
        public double getMaxHealth() {
            return 0.0;
        }

        @Override
        public void setMaxHealth(double maxHealth) {

        }

        @Override
        public long getRemainingAir() {
            return 0L;
        }

        @Override
        public void setRemainingAir(long ticks) {

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
        public EntityDamageEvent getLastDamageCause() {
            return null;
        }

        @Override
        public Player hurtByPlayer() {
            return null;
        }

        @Override
        public boolean isDead() {
            return false;
        }

        @Override
        public boolean isNameVisible() {
            return false;
        }

        @Override
        public void applyProperties(EntityProperties properties) {

        }

        @Override
        public <T extends Projectile> T launchProjectile(EntityProperties properties) {
            return null;
        }

        @Override
        public void sendMessage(String... messages) {
            // TODO - What is meant to go here?
        }

        @Override
        public float getFlyingSpeed() {
            return 0;
        }

        @Override
        public void setFlyingSpeed(float flyingSpeed) {

        }

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public ItemStack getItemInHand() {
            return null;
        }

        @Override
        public GameMode getGameMode() {
            return null;
        }

        @Override
        public float getMoveSpeed() {
            return 0;
        }

        @Override
        public void setMoveSpeed(float speed) {

        }

        @Override
        public float getSneakSpeed() {
            return 0;
        }

        @Override
        public void setSneakSpeed(float speed) {

        }

        @Override
        public float getWalkSpeed() {
            return 0;
        }

        @Override
        public void setWalkSpeed(float speed) {

        }
    }
}
