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
package net.tridentsdk.server.player;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.server.packet.PacketOut;
import net.tridentsdk.server.world.TridentChunk;
import net.tridentsdk.server.world.TridentWorld;

import javax.annotation.concurrent.Immutable;
import java.util.Set;

/**
 * Utility class that contains shortcuts for selecting
 * specific recipients of a particular packet.
 */
@Immutable
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RecipientSelector {
    /**
     * Sends the given packet to those who can see the given
     * entity, as well as the given entity too if the flag
     * is set to {@code true}.
     *
     * @param chunk the chunk in which holders will be
     * selected to receive the given packet
     * @param packetOut the packet to send to the selected
     * targets
     * @param exclude a non-null player if they should be
     * excluded from receiving this packet
     */
    public static void whoCanSee(TridentChunk chunk, PacketOut packetOut, Entity exclude) {
        if (chunk == null) {
            throw new IllegalStateException("Player cannot inhabit an unloaded chunk");
        }

        Set<TridentPlayer> targets = chunk.getHolders();
        if (exclude == null || !(exclude instanceof Player)) {
            for (TridentPlayer p : targets) {
                p.net().sendPacket(packetOut);
            }
        } else {
            for (TridentPlayer p : targets) {
                if (p.equals(exclude)) {
                    continue;
                }

                p.net().sendPacket(packetOut);
            }
        }
    }

    /**
     * Sends the given packet to those who can see the given
     * player.
     *
     * @param canSee the player that can be seen
     * @param packetOut the packet to send to selected
     * recipients
     * @param exclude whether or not to exclude the player
     * that can be seen from the recipients
     */
    public static void whoCanSee(TridentPlayer canSee, PacketOut packetOut, boolean exclude) {
        whoCanSee(canSee.getWorld().getChunkAt(canSee.getPosition().getChunkX(), canSee.getPosition().getChunkZ(), true),
                packetOut, exclude ? canSee : null);
    }

    /**
     * Sends the given packet to all players who are
     * occupants of the given world.
     *
     * @param world the world in which the players are the
     * target of the packet to be sent
     * @param packetOut the packet to send to the selected
     * players
     */
    public static void inWorld(TridentWorld world, PacketOut packetOut) {
        for (TridentPlayer player : world.getOccupants()) {
            player.net().sendPacket(packetOut);
        }
    }
}