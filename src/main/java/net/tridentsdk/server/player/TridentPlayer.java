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
import net.tridentsdk.Handler;
import net.tridentsdk.Position;
import net.tridentsdk.Trident;
import net.tridentsdk.base.Substance;
import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.event.player.PlayerJoinEvent;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.meta.MessageBuilder;
import net.tridentsdk.meta.nbt.CompoundTag;
import net.tridentsdk.meta.nbt.IntTag;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.data.MetadataType;
import net.tridentsdk.server.data.ProtocolMetadata;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static net.tridentsdk.server.packets.play.out.PacketPlayOutPlayerListItem.PlayerListDataBuilder;

@ThreadSafe
public class TridentPlayer extends OfflinePlayer {
    private static final Map<UUID, Player> ONLINE_PLAYERS = new ConcurrentHashMap<>();

    private final PlayerConnection connection;
    private final Set<ChunkLocation> knownChunks = Factories.collect().createSet();
    private final Queue<PacketPlayOutMapChunkBulk> chunkQueue = Queues.newConcurrentLinkedQueue();
    private volatile boolean loggingIn = true;
    private volatile boolean sprinting;
    private volatile boolean crouching;
    private volatile boolean flying;
    private volatile byte skinFlags;
    private volatile Locale locale;
    private volatile int viewDistance = 7;

    private TridentPlayer(UUID uuid, CompoundTag tag, TridentWorld world, ClientConnection connection) {
        super(uuid, tag, world);

        this.connection = PlayerConnection.createPlayerConnection(connection, this);
    }

    public static void sendAll(Packet packet) {
        players().stream().forEach((p) -> ((TridentPlayer) p).connection.sendPacket(packet));
    }

    public static void sendFiltered(Packet packet, Predicate<Player> predicate) {
        players().stream()
                .filter(predicate)
                .forEach((p) -> ((TridentPlayer) p).connection.sendPacket(packet));
    }

    public static TridentPlayer spawnPlayer(ClientConnection connection, UUID id, String name) {
        // determine if this player has logged in before
        CompoundTag playerTag = (OfflinePlayer.getOfflinePlayer(
                id) == null) ? null : OfflinePlayer.getOfflinePlayer(id).asNbt();

        // if this player is new
        if (playerTag == null) {
            playerTag = OfflinePlayer.generatePlayer(id);
        }

        final TridentPlayer p = new TridentPlayer(id, playerTag, TridentServer.WORLD, connection);
        p.executor = ThreadsHandler.playerExecutor();

        OfflinePlayer.OFFLINE_PLAYERS.put(id, p);
        ONLINE_PLAYERS.put(id, p);

        p.name = name;

        p.gameMode = GameMode.gamemodeOf(((IntTag)playerTag.getTag("playerGameType")).value());

        p.executor.execute(() -> {
            p.connection.sendPacket(new PacketPlayOutJoinGame().set("entityId", p.entityId())
                    .set("gamemode", p.gameMode)
                    .set("dimension", p.world().dimension())
                    .set("difficulty", p.world().difficulty())
                    .set("maxPlayers", (short) Trident.config().getInt("max-players"))
                    .set("levelType", LevelType.DEFAULT));

            p.abilities.creative = 1;
            p.abilities.flySpeed = 1;

            // DEBUG =====
            p.setLocation(new Position(p.world(), 0, 255, 0));
            p.spawnLocation = new Position(p.world(), 0, 255, 0);
            // =====

            p.connection.sendPacket(PacketPlayOutPluginMessage.VANILLA_CHANNEL);
            p.connection.sendPacket(new PacketPlayOutServerDifficulty().set("difficulty", p.world().difficulty()));
            p.connection.sendPacket(new PacketPlayOutSpawnPosition().set("location", p.spawnLocation()));
            p.connection.sendPacket(p.abilities.asPacket());
            Trident.server().logger().info("Flags:\n canFly: " + Boolean.toString(p.abilities.canFly()) + '\n' +
                    "godMode: " + Boolean.toString(p.abilities.isInvulnerable()) + '\n' +
                    "flying: " + Boolean.toString(p.abilities.isFlying()) + '\n' +
                    "creative: " + Boolean.toString(p.abilities.isCreative()));
            p.connection.sendPacket(new PacketPlayOutPlayerCompleteMove().set("location",
                    p.spawnLocation()).set("flags", (byte) 0));

            PacketPlayOutPlayerListItem packet = new PacketPlayOutPlayerListItem();

            packet.set("action", 0); // add a player
            packet.set("playerListData", new PlayerListDataBuilder[]{p.listData()});

            sendAll(new PacketPlayOutPlayerListItem()
                    .set("action", 0)
                    .set("playerListData", new PlayerListDataBuilder[]{p.listData()}));

            List<PlayerListDataBuilder> builders = new ArrayList<>();

            players().stream().filter((player) -> !player.equals(p))
                    .forEach((player) -> builders.add(((TridentPlayer) player).listData()));
            players().forEach((player) -> player.sendMessage(p.name + " has joined the server"));
            TridentLogger.log(p.name + " has joined the server");

            p.connection.sendPacket(new PacketPlayOutPlayerListItem()
                    .set("action", 0)
                    .set("playerListData", builders.stream().toArray(PlayerListDataBuilder[]::new)));
        });

        return p;
    }

    public static Player getPlayer(UUID id) {
        return ONLINE_PLAYERS.get(id);
    }

    public static Collection<Player> players() {
        return ONLINE_PLAYERS.values();
    }

    @Override
    protected void doEncodeMeta(ProtocolMetadata protocolMeta) {
        protocolMeta.setMeta(10, MetadataType.BYTE, skinFlags);
        protocolMeta.setMeta(16, MetadataType.BYTE, (byte) 0); // hide cape, might need changing
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
        sendChunks(viewDistance());
        connection.sendPacket(new PacketPlayOutPlayerCompleteMove().set("location",
                position()).set("flags", (byte) 1));

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
        Handler.forEvents().fire(new PlayerJoinEvent(this));
    }

    @Override
    public void tick() {
        ThreadsHandler.playerExecutor().execute(this::doTick);
    }

    @Override
    protected void doTick() {
        int distance = viewDistance();
        if (!isLoggingIn())
            sendChunks(distance);

        if (!chunkQueue.isEmpty())
            connection.sendPacket(chunkQueue.poll());

        connection.tick();
        ticksExisted.incrementAndGet();
    }

    public void cleanChunks(PacketPlayOutMapChunkBulk bulk) {
        int distance = viewDistance();

        for (ChunkLocation location : knownChunks) {
            if (Math.abs(location.x() - (position().x() / 16)) < distance ||
                    Math.abs(location.z() - (position().z() / 16)) < distance) {
                knownChunks.remove(location);
                bulk.addEntry(new PacketPlayOutChunkData(new byte[0], location, true, (short) 0));
            }
        }
    }

    @Override
    protected void doRemove() {
        ONLINE_PLAYERS.remove(this.uniqueId());
        players().forEach((player) -> player.sendRaw(name + " has left the server"));
        TridentLogger.log(name + " has left the server");
    }

    /*
     * @NotJavaDoc
     * TODO: Create Message API and utilize it
     */
    public void kickPlayer(String reason) {
        connection.sendPacket(new PacketPlayOutDisconnect().set("reason", reason));
        TridentLogger.log(name + " was kicked for " + reason);
    }

    public PlayerListDataBuilder listData() {
        return new PacketPlayOutPlayerListItem.PlayerListDataBuilder()
                .id(uniqueId)
                .values(name,
                        0, // properties, TODO
                        (int) gameMode.asByte(),
                        0,
                        displayName != null,
                        displayName);
    }

    public PlayerConnection connection() {
        return this.connection;
    }

    public void setSlot(final short slot) {
        if ((int) slot > 8 || (int) slot < 0) {
            TridentLogger.error(new IllegalArgumentException("Slot must be within the ranges of 0-8"));
            return;
        }

        TridentPlayer.super.selectedSlot = slot;
    }

    @Override
    public void sendMessage(String message) {
        sendRaw(new MessageBuilder(message)
                .build()
                .asJson());
    }

    @Override
    public void sendRaw(final String... messages) {
        Stream.of(messages)
                .filter((m) -> m != null)
                .forEach((message) -> connection.sendPacket(new PacketPlayOutChat()
                        .set("jsonMessage", message)
                        .set("position", PacketPlayOutChat.ChatPosition.CHAT)));
    }

    public void sendChunks(int viewDistance) {
        int centX = ((int) Math.floor(loc.x())) >> 4;
        int centZ = ((int) Math.floor(loc.z())) >> 4;
        PacketPlayOutMapChunkBulk bulk = new PacketPlayOutMapChunkBulk();
        int length = 0;

        for (int x = (centX - viewDistance / 2); x <= (centX + viewDistance / 2); x += 1) {
            zl:
            for (int z = (centZ - viewDistance / 2); z <= (centZ + viewDistance / 2); z += 1) {
                ChunkLocation location = ChunkLocation.create(x, z);

                for (ChunkLocation loc : knownChunks) {
                    if (loc.equals(location)) {
                        continue zl;
                    }
                }

                TridentChunk chunk = (TridentChunk) world().chunkAt(x, z, true);
                PacketPlayOutChunkData data = chunk.asPacket();

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

        if (bulk.hasEntries()) {
            connection.sendPacket(bulk);
        }
    }

    @Override
    public void setGameMode(GameMode mode) {
        super.setGameMode(mode);

        this.connection.sendPacket(this.abilities.asPacket());
    }

    public void setFlying(boolean flying) {
        this.flying = flying;

        abilities.flying = (flying) ? (byte) 1 : (byte) 0;

        connection.sendPacket(abilities.asPacket());
    }

    public boolean isFlying() {
        return flying;
    }

    public boolean isFlyMode() {
        return abilities.canFly();
    }

    public void setFlyMode(boolean flying) {
        abilities.canFly = (flying) ? (byte) 1: (byte) 0;
    }

    public void setSprinting(boolean sprinting) {
        this.sprinting = sprinting;
    }

    public boolean isSprinting() {
        return sprinting;
    }

    @InternalUseOnly
    public void setCrouching(boolean crouching) {
        this.crouching = crouching;
    }

    public boolean isCrouching() {
        return crouching;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setSkinFlags(byte flags) {
        skinFlags = flags;
    }

    public void setViewDistance(int viewDistance) {
        this.viewDistance = viewDistance;
    }

    private static final int MAX_VIEW = Trident.config().getInt("view-distance", 15);
    public int viewDistance() {
        return Math.min(viewDistance, MAX_VIEW);
    }
}
