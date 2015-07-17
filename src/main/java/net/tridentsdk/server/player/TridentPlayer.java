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

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.tridentsdk.GameMode;
import net.tridentsdk.Handler;
import net.tridentsdk.Position;
import net.tridentsdk.Trident;
import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.event.player.PlayerJoinEvent;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.meta.ChatColor;
import net.tridentsdk.meta.MessageBuilder;
import net.tridentsdk.meta.nbt.CompoundTag;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.data.MetadataType;
import net.tridentsdk.server.data.ProtocolMetadata;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.OutPacket;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.packets.play.out.*;
import net.tridentsdk.server.threads.ThreadsHandler;
import net.tridentsdk.server.world.TridentChunk;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.util.Vector;
import net.tridentsdk.world.ChunkLocation;
import net.tridentsdk.world.LevelType;

import javax.annotation.concurrent.ThreadSafe;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static net.tridentsdk.server.packets.play.out.PacketPlayOutPlayerListItem.PlayerListDataBuilder;

@ThreadSafe
public class TridentPlayer extends OfflinePlayer {
    private static final Map<UUID, Player> ONLINE_PLAYERS = new ConcurrentHashMap<>();
    private static final int MAX_VIEW = Trident.config().getInt("view-distance", 15);
    private static final int MAX_CHUNKS = (int) Trident.config().getConfigSection("performance")
            .getInt("max-chunks-player", 441);

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

        p.gameMode = GameMode.CREATIVE;//GameMode.gamemodeOf(((IntTag) playerTag.getTag("playerGameType")).value());

        p.executor.execute(() -> {
            p.connection.sendPacket(new PacketPlayOutJoinGame().set("entityId", p.entityId())
                    .set("gamemode", p.gameMode)
                    .set("dimension", p.world().dimension())
                    .set("difficulty", p.world().difficulty())
                    .set("maxPlayers", (short) Trident.config().getInt("max-players"))
                    .set("levelType", LevelType.DEFAULT));

            p.abilities.creative = 1;
            p.abilities.flySpeed = 0.135F;
            p.abilities.canFly = 1;

            // DEBUG =====
            p.setPosition(new Position(p.world(), 0, 255, 0));
            p.spawnLocation = new Position(p.world(), 0, 255, 0);
            // =====

            p.connection.sendPacket(PacketPlayOutPluginMessage.VANILLA_CHANNEL);
            p.connection.sendPacket(new PacketPlayOutServerDifficulty().set("difficulty", p.world().difficulty()));
            p.connection.sendPacket(new PacketPlayOutSpawnPosition().set("location", p.spawnLocation()));
            p.connection.sendPacket(p.abilities.asPacket());
            p.connection.sendPacket(new PacketPlayOutPlayerCompleteMove().set("location",
                    p.spawnLocation()).set("flags", (byte) 0));

            sendBetween(p, PacketPlayOutPlayerListItem::new, (player, packet) ->
                    packet.set("action", 0).set("playerListData", new PlayerListDataBuilder[]{player.listData()}), true);

            ProtocolMetadata metadata = new ProtocolMetadata();
            p.encodeMetadata(metadata);
        });

        p.spawn();
        return p;
    }

    public static Player getPlayer(UUID id) {
        return ONLINE_PLAYERS.get(id);
    }

    public static Collection<Player> players() {
        return ONLINE_PLAYERS.values();
    }

    private static <T extends OutPacket> void sendBetween(TridentPlayer newPlayer, Supplier<T> packetSupplier,
                                                          BiConsumer<TridentPlayer, T> consumer, boolean selfSend) {
        for (Player player : TridentPlayer.players()) {
            TridentPlayer p = (TridentPlayer) player;

            // If you aren't sending to self and the player is self
            // continue
            if (p.equals(newPlayer) && !selfSend) continue;

            // Send the packet to the newPlayer
            T packet = packetSupplier.get();
            consumer.accept(p, packet);
            newPlayer.connection.sendPacket(packet);

            // Send the packet to the current player
            if (p.equals(newPlayer)) continue; // Don't send to the same player twice
            T anotherPacket = packetSupplier.get();
            consumer.accept(newPlayer, anotherPacket);
            p.connection.sendPacket(anotherPacket);
        }
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

        //TridentWindow window = TridentWindow.create("Inventory", 18, InventoryType.CHEST);
        //window.setSlot(0, new Item(Substance.DIAMOND_PICKAXE));
        //window.sendTo(this);

        // Wait for response
        for (Entity entity : world().entities()) {
            // Register mob, packet sent to new player
        }

        loggingIn = false;
        connection.sendPacket(new PacketPlayOutEntityVelocity()
                .set("entityId", entityId())
                .set("velocity", new Vector(0, -0.07, 0)));
        connection.sendPacket(new PacketPlayOutGameStateChange().set("reason", 3).set("value", (float) gameMode().asByte()));
        TridentServer.WORLD.addEntity(this); // TODO
        Handler.forEvents().fire(new PlayerJoinEvent(this));

        sendBetween(this, PacketPlayOutSpawnPlayer::new, (p, packet) -> {
            ProtocolMetadata meta = new ProtocolMetadata();
            p.encodeMetadata(meta);

            packet.set("entityId", p.id)
                    .set("player", p)
                    .set("metadata", meta);
        }, false);
    }

    @Override
    protected void doTick() {
        int distance = viewDistance();
        if (!isLoggingIn())
            sendChunks(distance);

        if (!chunkQueue.isEmpty())
            connection.sendPacket(chunkQueue.poll());

        cleanChunks();

        connection.tick();
        ticksExisted.incrementAndGet();
    }

    public void cleanChunks() {
        int toClean = knownChunks.size() - MAX_CHUNKS;
        if (toClean > 0) {
            Position pos = position();
            int x = (int) pos.x() / 16;
            int z = (int) pos.z() / 16;
            int viewDist = viewDistance();

            for (ChunkLocation location : knownChunks) {
                int cx = location.x();
                int cz = location.z();

                int abs = Math.abs(cx - x);
                int abs1 = Math.abs(cz - z);

                if (abs > viewDist || abs1 > viewDist) {
                    ((TridentWorld) world()).loadedChunks.tryRemove(location);
                    connection.sendPacket(new PacketPlayOutChunkData(new byte[0], location, true, (short) 0));
                    knownChunks.remove(location);
                }
            }
        }
    }

    @Override
    protected void doRemove() {
        ONLINE_PLAYERS.remove(this.uniqueId());

        PacketPlayOutPlayerListItem item = new PacketPlayOutPlayerListItem();
        item.set("action", 4).set("playerListData", new PlayerListDataBuilder[]{
                new PacketPlayOutPlayerListItem.PlayerListDataBuilder().id(uniqueId).values(new Object[0])});
        sendAll(item);

        players().forEach(p ->
                new MessageBuilder(name + " has left the server").color(ChatColor.YELLOW).build().sendTo(p));
        TridentLogger.log(name + " has left the server");
    }

    @Override
    public void setPosition(Position loc) {
        players().stream()
                .filter((p) -> !p.equals(this))
                .forEach((p) -> {
                    ((TridentPlayer) p).connection.sendPacket(new PacketPlayOutEntityTeleport()
                            .set("entityId", entityId())
                            .set("location", loc)
                            .set("onGround", onGround));
                });

        /* double dX = loc.x() - position().x();
        double dY = loc.y() - position().y();
        double dZ = loc.z() - position().z();
        if (dX == 0 && dY == 0 && dZ == 0) {
            sendFiltered(new PacketPlayOutEntityLook().set("entityId", entityId())
                            .set("location", loc).set("onGround", onGround), player -> !player.equals(this)
                    );

            return;
        }

        if (dX > 4 || dY > 4 || dZ > 4) {
            sendFiltered(new PacketPlayOutEntityTeleport()
                    .set("entityId", entityId())
                    .set("location", loc)
                    .set("onGround", onGround), player -> !player.equals(this));
        } else {
            sendFiltered(new PacketPlayOutEntityRelativeMove()
                    .set("entityId", entityId())
                    .set("difference", new Vector(dX, dY, dZ))
                            //.set("yaw", loc.yaw())
                            //.set("pitch", loc.pitch())
                    .set("onGround", onGround), player -> !player.equals(this));
        } */

        super.setPosition(loc);
    }

    /*
         * @NotJavaDoc
         * TODO: Create Message API and utilize it
         */
    public void kickPlayer(String reason) {
        connection.sendPacket(new PacketPlayOutDisconnect().set("reason", new MessageBuilder(reason).build().asJson()));
        TridentLogger.log(name + " was kicked for " + reason);
    }

    private static final Map<UUID, JsonObject> textures = new ConcurrentHashMap<>();
    public PlayerListDataBuilder listData() {
        PlayerListDataBuilder builder = new PlayerListDataBuilder()
                .id(uniqueId)
                .values(name,
                        1, new Object[4],
                        (int) gameMode.asByte(),
                        0,
                        displayName != null,
                        displayName);
        texture(builder);
        return builder;
    }

    // TODO move to login
    private String texture(PlayerListDataBuilder listData) {
        JsonObject tex = textures.get(uniqueId());

        if (tex == null) {
            try {
                URL mojang = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" +
                        uniqueId.toString().replace("-", "") + "?unsigned=false");
                StringBuilder builder = new StringBuilder();
                URLConnection connection = mojang.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }

                JsonElement object = new JsonParser().parse(builder.toString());
                JsonArray properties = object.getAsJsonObject().get("properties").getAsJsonArray();

                for (int i = 0; i < properties.size(); i++) {
                    JsonObject element = properties.get(i).getAsJsonObject();
                    if (element.get("name").getAsString().equals("textures")) {
                        String value = element.get("value").getAsString();
                        byte[] base64decoded = Base64.getDecoder().decode(value);
                        JsonElement skinData = new JsonParser().parse(new String(base64decoded));
                        JsonObject textureObj = skinData.getAsJsonObject().get("textures").getAsJsonObject();
                        String skinUrl = textureObj.get("SKIN").getAsJsonObject().get("url").getAsString();

                        tex = object.getAsJsonObject();
                        textures.put(uniqueId(), object.getAsJsonObject());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        JsonObject textures = tex.getAsJsonArray("properties").get(0).getAsJsonObject();
        List<Object> objs = Lists.newArrayList(listData.values()[3]);
        objs.add("textures");
        objs.add(textures.get("value").getAsString());
        objs.add(true);
        objs.add(textures.get("signature").getAsString());
        listData.values()[3] = objs.toArray();
        listData.values(listData.values());

        return null;
    }

    public PlayerConnection connection() {
        return this.connection;
    }

    public static final int SLOT_OFFSET = 35;

    public void setSlot(final short slot) {
        if ((int) slot > 8 || (int) slot < 0) {
            TridentLogger.error(new IllegalArgumentException("Slot must be within the ranges of 0-8"));
            return;
        }

        TridentPlayer.super.selectedSlot = slot;

        setSelectedSlot(slot);
        setHeldItem(heldItem()); // Updates inventory
    }

    @Override
    public void sendMessage(String message) {
        new MessageBuilder(message)
                .build()
                .sendTo(this);
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
            for (int z = (centZ - viewDistance / 2); z <= (centZ + viewDistance / 2); z += 1) {
                ChunkLocation location = ChunkLocation.create(x, z);
                if (!knownChunks.add(location)) continue;

                TridentChunk chunk = (TridentChunk) world().chunkAt(x, z, true);
                PacketPlayOutChunkData data = chunk.asPacket();

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

    public boolean isFlying() {
        return flying;
    }

    public void setFlying(boolean flying) {
        this.flying = flying;

        abilities.flying = (flying) ? (byte) 1 : (byte) 0;

        connection.sendPacket(abilities.asPacket());
    }

    public boolean isFlyMode() {
        return abilities.canFly();
    }

    public void setFlyMode(boolean flying) {
        abilities.canFly = (flying) ? (byte) 1 : (byte) 0;
    }

    public boolean isSprinting() {
        return sprinting;
    }

    public void setSprinting(boolean sprinting) {
        this.sprinting = sprinting;
    }

    public boolean isCrouching() {
        return crouching;
    }

    @InternalUseOnly
    public void setCrouching(boolean crouching) {
        /* ProtocolMetadata meta = new ProtocolMetadata();
        encodeMetadata(meta);

        int idx = 0;
        int mask = 0x02;
        meta.setMeta(idx, MetadataType.BYTE, (byte) (((byte) meta.get(0).value() & ~mask) | (crouching ? mask : 0)));
        System.out.println(meta.get(0).value());
        sendFiltered(new PacketPlayOutEntityMetadata().set("entityId", entityId()).set("metadata", meta),
                p -> !p.equals(this)); */
        PacketPlayOutEffect effect = new PacketPlayOutEffect();
        effect.set("effectId", 104);

        this.crouching = crouching;
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

    public int viewDistance() {
        return Math.min(viewDistance, MAX_VIEW);
    }

    @Override
    public EntityType type() {
        return EntityType.PLAYER;
    }
}
