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
package net.tridentsdk.player;

import io.netty.util.internal.ConcurrentSet;
import net.tridentsdk.api.Trident;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.factory.Factories;
import net.tridentsdk.api.nbt.CompoundTag;
import net.tridentsdk.api.threads.TaskExecutor;
import net.tridentsdk.api.world.LevelType;
import net.tridentsdk.packets.play.out.*;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.world.TridentChunk;
import net.tridentsdk.world.TridentWorld;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class TridentPlayer extends OfflinePlayer {
    private static final Set<TridentPlayer> players = new ConcurrentSet<>();

    private final PlayerConnection connection;
    private final TaskExecutor executor = Factories.threads().playerThread(this);
    private volatile Locale locale;

    public TridentPlayer(CompoundTag tag, TridentWorld world, ClientConnection connection) {
        super(tag, world);

        this.connection = PlayerConnection.createPlayerConnection(connection, this);
    }

    public static void sendAll(Packet packet) {
        for (TridentPlayer p : players) {
            p.connection.sendPacket(packet);
        }
    }

    public static Player spawnPlayer(ClientConnection connection, UUID id, String name) {
        CompoundTag offlinePlayer = (OfflinePlayer.getOfflinePlayer(id) == null) ? null :
                OfflinePlayer.getOfflinePlayer(id).toNbt();

        if(offlinePlayer == null) {
            offlinePlayer = OfflinePlayer.generatePlayer(name, id);
        }

        TridentPlayer p = new TridentPlayer(offlinePlayer,
                (TridentWorld) Trident.getWorlds().iterator().next(), connection);

        p.connection.sendPacket(new PacketPlayOutJoinGame().set("entityId", p.getId())
                .set("gamemode", p.getGameMode())
                .set("dimension", ((TridentWorld) p.getWorld()).getDimesion())
                .set("difficulty", p.getWorld().getDifficulty())
                .set("maxPlayers", (short) 10)
                .set("levelType",
                        LevelType.DEFAULT));

        p.connection.sendPacket(new PacketPlayOutSpawnPosition().set("location", p.getSpawnLocation()));
        p.connection.sendPacket(p.abilities.toPacket());
        p.connection.sendPacket(new PacketPlayOutPlayerCompleteMove().set("location", p.getLocation())
                .set("flags", (byte) 0));

        p.sendChunks(7);


        players.add(p);

        return p;
    }

    public static TridentPlayer getPlayer(UUID id) {
        for (TridentPlayer player : players) {
            if (player.getUniqueId().equals(id)) {
                return player;
            }
        }

        return null;
    }

    @Override
    public void tick() {
        this.executor.addTask(new Runnable() {
            @Override
            public void run() {
                TridentPlayer.super.tick();

                if (TridentPlayer.this.connection.getKeepAliveId() == -1) {
                    // send Keep Alive packet if not sent already
                    PacketPlayOutKeepAlive packet = new PacketPlayOutKeepAlive();

                    TridentPlayer.this.connection.sendPacket(packet);
                    TridentPlayer.this.connection.setKeepAliveId(packet.getKeepAliveId(),
                            TridentPlayer.this.ticksExisted.get());
                } else if (TridentPlayer.this.ticksExisted.get() -
                        TridentPlayer.this.connection.getKeepAliveSent() >= 600L) {
                    // kick the player for not responding to the keep alive within 30 seconds/600 ticks
                    TridentPlayer.this.kickPlayer("Timed out!");
                }
            }
        });
    }

    /*
     * @NotJavaDoc
     * TODO: Create Message API and utilize it
     */
    public void kickPlayer(final String reason) {
        this.executor.addTask(new Runnable() {
            @Override
            public void run() {
                TridentPlayer.this.connection.sendPacket(new PacketPlayOutDisconnect().set("reason", reason));
                TridentPlayer.this.connection.logout();
            }
        });
    }

    public PlayerConnection getConnection() {
        return this.connection;
    }

    public void setSlot(final short slot) {
        this.executor.addTask(new Runnable() {
            @Override
            public void run() {
                if ((int) slot > 8 || (int) slot < 0) {
                    throw new IllegalArgumentException("Slot must be within the ranges of 0-8");
                }

                TridentPlayer.super.selectedSlot = slot;
            }
        });
    }

    @Override
    public void sendMessage(final String... messages) {
        // TODO: Verify proper implementation
        this.executor.addTask(new Runnable() {
            @Override
            public void run() {
                for (String message : messages) {
                    if (message != null) {
                        TridentPlayer.this.connection.sendPacket(new PacketPlayOutChatMessage().set("jsonMessage",
                                message)
                                .set("position", PacketPlayOutChatMessage.ChatPosition.CHAT));
                    }
                }
            }
        });
    }

    private void sendChunks(int viewDistance) {
        int centX = ((int) Math.floor(loc.getX())) >> 4;
        int centZ = ((int) Math.floor(loc.getZ())) >> 4;

        for (int x = (centX - viewDistance); x <= (centX + viewDistance); x += 1) {
            for (int z = (centZ - viewDistance); z <= (centZ + viewDistance); z += 1) {
                connection.sendPacket(((TridentChunk) getWorld().getChunkAt(x, z, true)).toPacket());
            }
        }
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
