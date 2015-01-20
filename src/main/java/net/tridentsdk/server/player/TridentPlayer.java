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

package net.tridentsdk.server.player;

import net.tridentsdk.GameMode;
import net.tridentsdk.base.Substance;
import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.meta.nbt.CompoundTag;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.entity.TridentEntityBuilder;
import net.tridentsdk.entity.ParameterValue;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.packets.play.out.*;
import net.tridentsdk.server.threads.ThreadsHandler;
import net.tridentsdk.server.window.TridentWindow;
import net.tridentsdk.server.world.TridentChunk;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.util.Vector;
import net.tridentsdk.window.inventory.InventoryType;
import net.tridentsdk.window.inventory.Item;
import net.tridentsdk.world.ChunkLocation;
import net.tridentsdk.world.LevelType;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@ThreadSafe
public class TridentPlayer extends OfflinePlayer {
    private final PlayerConnection connection;
    private final Set<ChunkLocation> knownChunks = Factories.collect().createSet();
    private volatile boolean loggingIn = true;
    private volatile Locale locale;

    public TridentPlayer(CompoundTag tag, TridentWorld world, ClientConnection connection) {
        super(tag, world);

        this.connection = PlayerConnection.createPlayerConnection(connection, this);
    }

    public static void sendAll(Packet packet) {
        for (Player p : getPlayers()) {
            ((TridentPlayer) p).connection.sendPacket(packet);
        }
    }

    public static Player spawnPlayer(ClientConnection connection, UUID id) {
        CompoundTag offlinePlayer = (OfflinePlayer.getOfflinePlayer(
                id) == null) ? null : OfflinePlayer.getOfflinePlayer(id).asNbt();

        if (offlinePlayer == null) {
            offlinePlayer = OfflinePlayer.generatePlayer(id);
        }

        final TridentPlayer p = TridentEntityBuilder.create().uuid(id).spawnLocation(
                TridentServer.WORLD.spawnLocation()) // TODO this is temporary for testing
                .executor(ThreadsHandler.playerExecutor())
                .build(TridentPlayer.class, ParameterValue.from(CompoundTag.class, offlinePlayer),
                        // TODO this is temporary for testing
                        ParameterValue.from(TridentWorld.class, TridentServer.WORLD),
                        ParameterValue.from(ClientConnection.class, connection));

        p.executor.execute(new Runnable() {
            @Override
            public void run() {
                p.connection.sendPacket(new PacketPlayOutJoinGame().set("entityId", p.entityId())
                        .set("gamemode", GameMode.CREATIVE)
                        .set("dimension", p.world().dimension())
                        .set("difficulty", p.world().difficulty())
                        .set("maxPlayers", (short) 10)
                        .set("levelType", LevelType.DEFAULT));

                p.connection.sendPacket(PacketPlayOutPluginMessage.VANILLA_CHANNEL);
                p.connection.sendPacket(new PacketPlayOutServerDifficulty().set("difficulty", p.world().difficulty()));
                p.connection.sendPacket(new PacketPlayOutSpawnPosition().set("location", p.getSpawnLocation()));
                p.connection.sendPacket(p.abilities.asPacket());
                p.connection.sendPacket(new PacketPlayOutPlayerCompleteMove().set("location",
                        p.getSpawnLocation().add(new Vector(0, 80, 0))).set("flags", (byte) 0));
            }
        });

        return p;
    }

    public static Player getPlayer(UUID id) {
        for (Player player : getPlayers()) {
            if (player.uniqueId().equals(id)) {
                return player;
            }
        }

        return null;
    }

    public static Collection<Player> getPlayers() {
        return Factories.threads().players();
    }

    public boolean isLoggingIn() {
        return loggingIn;
    }

    @InternalUseOnly
    public void resumeLogin() {
        if (!loggingIn)
            return;

        sendChunks(7);
        connection.sendPacket(PacketPlayOutStatistics.DEFAULT_STATISTIC);

        TridentWindow window = new TridentWindow("Inventory", 9, InventoryType.CHEST);
        window.setSlot(0, new Item(Substance.DIAMOND_PICKAXE));
        window.sendTo(this);

        // Wait for response
        for (Entity entity : world().entities()) {
            // Register mob, packet sent to new player
        }

        loggingIn = false;
    }

    @Override
    public void tick() {
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                TridentPlayer.super.tick();

                sendChunks(TridentServer.instance().viewDistance());
                connection.tick();
                ticksExisted.incrementAndGet();
            }
        });
    }

    /*
     * @NotJavaDoc
     * TODO: Create Message API and utilize it
     */
    public void kickPlayer(final String reason) {
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                TridentPlayer.this.connection.sendPacket(new PacketPlayOutDisconnect().set("reason", reason));
            }
        });
    }

    public PlayerConnection getConnection() {
        return this.connection;
    }

    public void setSlot(final short slot) {
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                if ((int) slot > 8 || (int) slot < 0) {
                    TridentLogger.error(new IllegalArgumentException("Slot must be within the ranges of 0-8"));
                }

                TridentPlayer.super.selectedSlot = slot;
            }
        });
    }

    @Override
    public void sendRaw(final String... messages) {
        // TODO: Verify proper implementation
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                for (String message : messages) {
                    if (message != null) {
                        TridentPlayer.this.connection.sendPacket(
                                new PacketPlayOutChatMessage().set("jsonMessage", message)
                                        .set("position", PacketPlayOutChatMessage.ChatPosition.CHAT));
                    }
                }
            }
        });
    }

    public void sendChunks(int viewDistance) {
        int centX = ((int) Math.floor(loc.getX())) >> 4;
        int centZ = ((int) Math.floor(loc.getZ())) >> 4;
        PacketPlayOutMapChunkBulk bulk = new PacketPlayOutMapChunkBulk();
        int length = 0;

        for (int x = (centX - (int) Math.floor(viewDistance / 2)); x <= (centX + (int) Math.floor(viewDistance / 2));
             x += 1) {
            for (int z = (centZ - (int) Math.floor(viewDistance / 2));
                 z <= (centZ + (int) Math.floor(viewDistance / 2)); z += 1) {
                ChunkLocation location = ChunkLocation.create(x, z);

                if (knownChunks.contains(location))
                    continue;

                PacketPlayOutChunkData data = ((TridentChunk) world().chunkAt(x, z, true)).asPacket();

                length += (10 + data.getData().length);

                bulk.addEntry(data);
                knownChunks.add(location);

                if (length >= 0x1DAE40) { // send the packet if the length is close to the protocol maximum
                    connection.sendPacket(bulk);

                    bulk = new PacketPlayOutMapChunkBulk();
                    length = 0;
                }
            }
        }

        connection.sendPacket(bulk);
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
