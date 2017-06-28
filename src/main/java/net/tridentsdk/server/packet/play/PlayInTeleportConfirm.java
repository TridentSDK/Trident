/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
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
package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.PacketIn;
import net.tridentsdk.util.Cache;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import static net.tridentsdk.server.net.NetData.rvint;

/**
 * Teleportation confirm packet. Sent after packets such as
 * {@link PlayOutPosLook} to confirm the ID.
 */
@ThreadSafe
public final class PlayInTeleportConfirm extends PacketIn {
    /**
     * The teleport ID cache
     */
    private static final Cache<NetClient, IdBlock> TELEPORT_ID =
            new Cache<>(NetClient.KEEP_ALIVE_KICK_NANOS / 1000000, (client, id) -> {
                if (id.shouldRemove()) {
                    client.disconnect("No teleport response");
                    return true;
                }

                return false;
            });

    /**
     * A block of reserved IDs for a particular client
     */
    private static class IdBlock {
        /**
         * The ID counter for the max reserved block
         */
        private volatile int counter;
        /**
         * The IDs that have been checked in
         */
        private volatile int checkedIn;

        // Field updaters to save memory
        private static final AtomicIntegerFieldUpdater<IdBlock> COUNTER =
                AtomicIntegerFieldUpdater.newUpdater(IdBlock.class, "counter");
        private static final AtomicIntegerFieldUpdater<IdBlock> CHECK_IN =
                AtomicIntegerFieldUpdater.newUpdater(IdBlock.class, "checkedIn");

        /**
         * Obtains the next ID value in this block.
         *
         * @return the next ID
         */
        int checkOut() {
            return COUNTER.getAndAdd(this, 1);
        }

        /**
         * Invalidates the block portion that has been
         * already reserved when a confirmation packet is
         * received.
         *
         * @param id the id to invalidate
         * @return {@code true} if the ID is good,
         * {@code false} if it has not been reserved
         */
        boolean checkIn(int id) {
            boolean good = COUNTER.get(this) >= id;
            if (good) {
                CHECK_IN.addAndGet(this, 1);
            }

            return good;
        }

        /**
         * Checks to see whether or not this block has been
         * fully checked in and should be removed to make
         * space for another block.
         *
         * @return {@code true} if the block should be
         * removed
         */
        boolean shouldRemove() {
            int done = CHECK_IN.get(this);
            int cur = COUNTER.get(this);
            return done == cur;
        }
    }

    /**
     * Obtains the next teleport ID for the given net
     * client
     *
     * @param client the client
     * @return the next teleport ID
     */
    public static int query(NetClient client) {
        IdBlock block = TELEPORT_ID.get(client, IdBlock::new);
        return block.checkOut();
    }

    public PlayInTeleportConfirm() {
        super(PlayInTeleportConfirm.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        int id = rvint(buf);
        IdBlock block = TELEPORT_ID.get(client);

        if (block != null) {
            if (block.checkIn(id)) {
                client.getPlayer().resumeLogin();
            } else {
                client.disconnect("Mismatched confirmation ID (" + id + ')');
            }
        }
    }
}