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

import com.google.common.collect.Queues;
import net.tridentsdk.GameMode;
import net.tridentsdk.Position;
import net.tridentsdk.base.Substance;
import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.ParameterValue;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.meta.MessageBuilder;
import net.tridentsdk.meta.nbt.CompoundTag;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.data.MetadataType;
import net.tridentsdk.server.data.ProtocolMetadata;
import net.tridentsdk.server.entity.TridentEntityBuilder;
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
import java.util.*;
import java.util.stream.Stream;

@ThreadSafe
public class TridentPlayer extends OfflinePlayer {
    private final PlayerConnection connection;
    private final Set<ChunkLocation> knownChunks = Factories.collect().createSet();
    private final Queue<PacketPlayOutMapChunkBulk> chunkQueue = Queues.newConcurrentLinkedQueue();
    private volatile boolean loggingIn = true;
    private volatile boolean sprinting;
    private volatile boolean crouching;
    private volatile boolean flying;
    private volatile byte skinFlags;
    private volatile Locale locale;

    public TridentPlayer(CompoundTag tag, TridentWorld world, ClientConnection connection) {
        super(tag, world);

        this.connection = PlayerConnection.createPlayerConnection(connection, this);
    }

    public static void sendAll(Packet packet) {
        for (Player p : players()) {
            ((TridentPlayer) p).connection.sendPacket(packet);
        }
    }

    public static TridentPlayer spawnPlayer(ClientConnection connection, UUID id, String name) {
        CompoundTag offlinePlayer = (OfflinePlayer.getOfflinePlayer(
                id) == null) ? null : OfflinePlayer.getOfflinePlayer(id).asNbt();

        if (offlinePlayer == null) {
            offlinePlayer = OfflinePlayer.generatePlayer(id);
        }

        final TridentPlayer p = TridentEntityBuilder.create().uuid(id).spawn(new Position(TridentServer.WORLD, 0, 255, 0))//TridentServer.WORLD.spawnLocation()) // TODO this is temporary for testing
                .executor(ThreadsHandler.playerExecutor())
                .build(TridentPlayer.class, ParameterValue.from(CompoundTag.class, offlinePlayer),
                        // TODO this is temporary for testing
                        ParameterValue.from(TridentWorld.class, TridentServer.WORLD),
                        ParameterValue.from(ClientConnection.class, connection));

        p.name = name;

        p.executor.execute(() -> {
            p.connection.sendPacket(new PacketPlayOutJoinGame().set("entityId", p.entityId())
                    .set("gamemode", GameMode.CREATIVE)
                    .set("dimension", p.world().dimension())
                    .set("difficulty", p.world().difficulty())
                    .set("maxPlayers", (short) 10)
                    .set("levelType", LevelType.DEFAULT));

            p.gameMode = GameMode.CREATIVE;
            p.abilities.instantBreak = 1;
            p.abilities.flySpeed = 1;

            // DEBUG =====
            p.setLocation(new Position(p.world(),0,255,0));
            p.spawnLocation = new Position(p.world(), 0,255,0);
            // =====

            p.connection.sendPacket(PacketPlayOutPluginMessage.VANILLA_CHANNEL);
            p.connection.sendPacket(new PacketPlayOutServerDifficulty().set("difficulty", p.world().difficulty()));
            p.connection.sendPacket(new PacketPlayOutSpawnPosition().set("location", p.spawnLocation()));
            p.connection.sendPacket(p.abilities.asPacket());
            p.connection.sendPacket(new PacketPlayOutPlayerCompleteMove().set("location",
                    p.spawnLocation().add(new Vector(0, 255, 0))).set("flags", (byte) 1));
        });

        return p;
    }

    public static Player getPlayer(UUID id) {
        for (Player player : players()) {
            if (player.uniqueId().equals(id)) {
                return player;
            }
        }

        return null;
    }

    public static Collection<Player> players() {
        return Factories.threads().players();
    }

    @Override
    protected void encodeMetadata(ProtocolMetadata protocolMeta) {
        super.encodeMetadata(protocolMeta);

        protocolMeta.setMeta(10, MetadataType.BYTE, skinFlags);
        protocolMeta.setMeta(16, MetadataType.BYTE, 0); // hide cape, might need changing
        protocolMeta.setMeta(17, MetadataType.FLOAT, 0F); // absorption hearts TODO
        protocolMeta.setMeta(18, MetadataType.INT, 0); // TODO scoreboard system (this value is the player's score)
    }

    public boolean isLoggingIn() {
        return loggingIn;
    }

    @InternalUseOnly
    public void resumeLogin() {
        if (!loggingIn)
            return;

        connection.sendPacket(PacketPlayOutStatistics.DEFAULT_STATISTIC);
        sendChunks(TridentServer.instance().viewDistance());
        connection.sendPacket(new PacketPlayOutPlayerCompleteMove().set("location",
                location()).set("flags", (byte) 1));

        TridentWindow window = TridentWindow.create("Inventory", 9, InventoryType.CHEST);
        window.setSlot(0, new Item(Substance.DIAMOND_PICKAXE));
        window.sendTo(this);

        // Wait for response
        for (Entity entity : world().entities()) {
            // Register mob, packet sent to new player
        }

        loggingIn = false;
        connection.sendPacket(new PacketPlayOutEntityVelocity()
                .set("entityId", entityId())
                .set("velocity", new Vector(0, -0.07, 0)));
        connection.sendPacket(new PacketPlayOutGameStateChange().set("reason", 3).set("value", (float) gameMode.asByte()));
        TridentServer.WORLD.addEntity(this); // TODO
    }

    @Override
    protected void doTick() {
        TridentPlayer.super.tick();

        if(!isLoggingIn())
            sendChunks(TridentServer.instance().viewDistance());

        if(!chunkQueue.isEmpty())
            connection.sendPacket(chunkQueue.poll());

        connection.tick();
        ticksExisted.incrementAndGet();
    }

    public void setSkinFlags(byte flags) {
        this.executor.execute(() -> skinFlags = flags);
    }

    /*
     * @NotJavaDoc
     * TODO: Create Message API and utilize it
     */
    public void kickPlayer(String reason) {
        this.executor.execute(() -> connection.sendPacket(new PacketPlayOutDisconnect().set("reason", reason)));
    }

    public PlayerConnection connection() {
        return this.connection;
    }

    public void setSlot(final short slot) {
        this.executor.execute(() -> {
            if ((int) slot > 8 || (int) slot < 0) {
                TridentLogger.error(new IllegalArgumentException("Slot must be within the ranges of 0-8"));
                return;
            }

            TridentPlayer.super.selectedSlot = slot;
        });
    }

    public void setSprinting(boolean sprinting) {
        this.sprinting = sprinting;
    }

    public void setFlying(boolean flying) {
        executor.execute(() -> {
            this.flying = flying;

            abilities.flying = (flying) ? (byte) 1 : (byte) 0;
            connection.sendPacket(abilities.asPacket());
        });
    }

    public boolean isFlying() {
        return flying;
    }

    public boolean isSprinting() {
        return sprinting;
    }

    public boolean isCrouching() {
        return crouching;
    }

    @InternalUseOnly
    public void setCrouching(boolean crouching) {
        this.crouching = crouching;
    }

    @Override
    public void sendMessage(String message) {
        sendRaw(new MessageBuilder(message)
                .build()
                .asJson());
    }

    @Override
    public void sendRaw(final String... messages) {
        executor.execute(() -> Stream.of(messages)
                .filter((m) -> m != null)
                .forEach((message) -> connection.sendPacket(new PacketPlayOutChat()
                        .set("jsonMessage", message)
                        .set("position", PacketPlayOutChat.ChatPosition.CHAT))));
    }

    public void sendChunks(int viewDistance) {
        int centX = ((int) Math.floor(loc.x())) >> 4;
        int centZ = ((int) Math.floor(loc.z())) >> 4;
        PacketPlayOutMapChunkBulk bulk = new PacketPlayOutMapChunkBulk();
        int length = 0;

        for (int x = (centX - viewDistance / 2); x <= (centX + viewDistance / 2); x += 1) {
            zl: for (int z = (centZ - viewDistance / 2); z <= (centZ + viewDistance / 2); z += 1) {
                ChunkLocation location = ChunkLocation.create(x, z);

                for(ChunkLocation loc : knownChunks) {
                    if(loc.equals(location)) {
                        continue zl;
                    }
                }

                PacketPlayOutChunkData data = ((TridentChunk) world().chunkAt(x, z, true)).asPacket();
                while (data == null) {
                    // Unfortunately this is necessary
                    // it's not consistent enough
                    data = ((TridentChunk) world().chunkAt(x, z, true)).asPacket();
                }

                knownChunks.add(location);
                bulk.addEntry(data);
                length += (10 + data.data().length);

                if (length >= 1845152) { // send the packet if the length is close to the protocol maximum
                    connection.sendPacket(bulk);

                    bulk = new PacketPlayOutMapChunkBulk();
                    length = 0;
                }
            }
        }

        if(bulk.hasEntries())
            connection.sendPacket(bulk);
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
