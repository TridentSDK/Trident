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
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.tridentsdk.Trident;
import net.tridentsdk.base.Block;
import net.tridentsdk.base.Position;
import net.tridentsdk.base.Substance;
import net.tridentsdk.config.ConfigSection;
import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.event.player.PlayerJoinEvent;
import net.tridentsdk.event.player.PlayerMoveEvent;
import net.tridentsdk.inventory.inventory.InventoryType;
import net.tridentsdk.inventory.inventory.Item;
import net.tridentsdk.meta.ChatColor;
import net.tridentsdk.meta.MessageBuilder;
import net.tridentsdk.meta.nbt.CompoundTag;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.concurrent.ThreadsHandler;
import net.tridentsdk.server.data.MetadataType;
import net.tridentsdk.server.data.ProtocolMetadata;
import net.tridentsdk.server.event.EventProcessor;
import net.tridentsdk.server.inventory.TridentInventory;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.packets.play.out.*;
import net.tridentsdk.server.world.TridentChunk;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.util.Vector;
import net.tridentsdk.world.ChunkLocation;
import net.tridentsdk.world.settings.GameMode;
import net.tridentsdk.world.settings.LevelType;

import javax.annotation.concurrent.ThreadSafe;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static net.tridentsdk.server.packets.play.out.PacketPlayOutPlayerListItem.PlayerListDataBuilder;

@ThreadSafe
public class TridentPlayer extends OfflinePlayer {
    private static final Map<UUID, Player> ONLINE_PLAYERS = new ConcurrentHashMap<>();
    private static final int MAX_VIEW = Trident.config().getInt("view-distance", 15);
    private static final ConfigSection tridentCfg = Trident.config().getConfigSection("performance");
    private static final int MAX_CHUNKS = tridentCfg.getInt("max-chunks-player", 441);
    private static final int CLEAN_ITERATIONS = tridentCfg.getInt("chunk-clean-iterations-player", 2);

    private final PlayerConnection connection;
    private final Set<ChunkLocation> knownChunks = Sets.newConcurrentHashSet();
    private final Queue<PacketPlayOutMapChunkBulk> chunkQueue = Queues.newConcurrentLinkedQueue();
    private volatile boolean loggingIn = true;
    private volatile boolean sprinting;
    private volatile boolean crouching;
    private volatile boolean flying;
    private volatile byte skinFlags;
    private volatile Locale locale;
    private volatile int viewDistance = MAX_VIEW;

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

        p.gameMode = GameMode.CREATIVE;//GameMode.of(((IntTag) playerTag.getTag("playerGameType")).value());

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
            // TODO: Use chunk HeightMap to get the spawn block more efficiently
            Block block = p.world().blockAt(Position.create(p.world(), 0, 255, 0));
            Vector below = new Vector(0, -1, 0);
            while(block.substance() == Substance.AIR
                    && block.relativeBlock(below).substance() == Substance.AIR) {
                block = block.relativeBlock(below);
            }

            block = block.relativeBlock(new Vector(0, 5, 0));
            //Position pos = Position.create(p.world(), 0, 255, 0);
            p.setPosition(block.position());
            p.spawnPosition = block.position();
            // =====

            p.connection.sendPacket(PacketPlayOutPluginMessage.VANILLA_CHANNEL);
            p.connection.sendPacket(new PacketPlayOutServerDifficulty().set("difficulty", p.world().difficulty()));
            p.connection.sendPacket(new PacketPlayOutSpawnPosition().set("location", p.spawnLocation()));
            p.connection.sendPacket(p.abilities.asPacket());
            p.connection.sendPacket(new PacketPlayOutPlayerCompleteMove().set("location",
                    p.spawnLocation()).set("flags", (byte) 0));

            sendAll(new PacketPlayOutPlayerListItem()
                    .set("action", 0)
                    .set("playerListData", new PlayerListDataBuilder[]{p.listData()}));

            List<PlayerListDataBuilder> builders = new ArrayList<>();

            players().stream().filter((player) -> !player.equals(p))
                    .forEach((player) -> builders.add(((TridentPlayer) player).listData()));
            TridentLogger.log(p.name + " has joined the server");

            p.connection.sendPacket(new PacketPlayOutPlayerListItem()
                    .set("action", 0)
                    .set("playerListData", builders.stream().toArray(PlayerListDataBuilder[]::new)));
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

    @Override
    protected void doEncodeMeta(ProtocolMetadata protocolMeta) {
        protocolMeta.setMeta(0, MetadataType.BYTE, (byte) (((fireTicks.intValue() == 0) ? 1 : 0) | (isCrouching() ? 2 : 0)
                | (isSprinting() ? 8 : 0))); // TODO invisibility & blocking/eating
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

        TridentInventory inventory = TridentInventory.create("Inventory", 18, InventoryType.CHEST);
        inventory.setSlot(0, new Item(Substance.DIAMOND_PICKAXE));
        inventory.sendTo(this);

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
        EventProcessor.fire(new PlayerJoinEvent(this));

        for (Player player : players()) {
            TridentPlayer p = (TridentPlayer) player;
            new MessageBuilder(name + " has joined the server").color(ChatColor.YELLOW).build().sendTo(player);

            if (!p.equals(this)) {
                ProtocolMetadata metadata = new ProtocolMetadata();
                encodeMetadata(metadata);

                p.connection.sendPacket(new PacketPlayOutSpawnPlayer()
                        .set("entityId", id)
                        .set("player", this)
                        .set("metadata", metadata));

                metadata = new ProtocolMetadata();
                p.encodeMetadata(metadata);
                connection.sendPacket(new PacketPlayOutSpawnPlayer()
                        .set("entityId", p.id)
                        .set("player", p)
                        .set("metadata", metadata));
            }
        }
    }

    @Override
    protected void doTick() {
        int distance = viewDistance();
        if (!isLoggingIn())
            sendChunks(distance);

        if (!chunkQueue.isEmpty())
            connection.sendPacket(chunkQueue.poll());

        for (int i = 0; i < CLEAN_ITERATIONS; i++) {
            if (knownChunks.size() > MAX_CHUNKS) {
                cleanChunks(distance - i);
            } else break;
        }

        connection.tick();
        ticksExisted.incrementAndGet();
    }

    public void cleanChunks(int viewDist) {
        Position pos = position();
        int x = (int) pos.x() / 16;
        int z = (int) pos.z() / 16;

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

    @Override
    protected void doRemove() {
        ONLINE_PLAYERS.remove(this.uniqueId());
        cleanChunks(0);

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
        double dX = loc.x() - position().x();
        double dY = loc.y() - position().y();
        double dZ = loc.z() - position().z();

        PlayerMoveEvent event = EventProcessor.fire(new PlayerMoveEvent(this, this.position(), loc));

        if (event.isIgnored()) {
            PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport();

            packet.set("entityId", this.entityId());
            packet.set("location", this.position());
            packet.set("onGround", this.onGround());

            connection.sendPacket(packet);
            return;
        }

        super.setPosition(loc);

        if (dX == 0 && dY == 0 && dZ == 0) {
            sendFiltered(new PacketPlayOutEntityLook().set("entityId", entityId())
                            .set("location", loc).set("onGround", onGround), player -> !player.equals(this)
                    );

            return;
        }

        if ((dX > 4 || dY > 4 || dZ > 4) || (ticksExisted.get() & 1) == 0) {
            sendFiltered(new PacketPlayOutEntityTeleport()
                    .set("entityId", entityId())
                    .set("location", loc)
                    .set("onGround", onGround), player -> !player.equals(this));
        } else {
            for (Player player : players()) {
                if (player.equals(this)) continue;

                Packet packet = new PacketPlayOutEntityRelativeMove()
                        .set("entityId", entityId())
                        .set("difference", new Vector(dX, dY, dZ))
                        .set("onGround", onGround);

                ((TridentPlayer) player).connection.sendPacket(packet);
            }
        }
    }

    /*
         * @NotJavaDoc
         * TODO: Create Message API and utilize it
         */
    public void kickPlayer(String reason) {
        connection.sendPacket(new PacketPlayOutDisconnect().set("reason", new MessageBuilder(reason).build().asJson()));
        TridentLogger.log(name + " was kicked for " + reason);
    }

    private static final Map<UUID, String> textures = new ConcurrentHashMap<>();
    public PlayerListDataBuilder listData() {
        String[] texture = texture().split("#");
        return new PacketPlayOutPlayerListItem.PlayerListDataBuilder()
                .id(uniqueId)
                .values(name,
                        1, new Object[]{"textures", texture[0], true, texture[1]},
                        (int) gameMode.asByte(),
                        0,
                        displayName != null,
                        displayName);
    }

    // TODO move to login
    private String texture() {
        String tex = textures.get(uniqueId());

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
                        String sig = element.get("signature").getAsString();

                        tex = value + "#" + sig;
                        textures.put(uniqueId(), tex);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return tex;
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
        // fixme
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

        // WARNING: The stability of this method is very, very poor
        // there are two bugs to fix should you choose to implement this

        // One: Spread out the chunk packet sending
        // the client usually drops about 50 frames at regular intervals
        // using this method

        // Two: Update clutter
        // There are more updates than chunks sent
        // perhaps consider a local collection that is pushed at the end
        // as the knownChunks is simply not enough to deal with the
        // incredibly fast chunk serialization via this method, or the
        // algorithm is completely incorrect in the first place

        // This method is so quick that the most likely bottleneck is the
        // client or the network

        /* int centX = ((int) Math.floor(loc.x())) >> 4;
        int centZ = ((int) Math.floor(loc.z())) >> 4;
        ThreadLocal<PacketPlayOutMapChunkBulk> bulk = new ThreadLocal<PacketPlayOutMapChunkBulk>() {
            @Override
            protected PacketPlayOutMapChunkBulk initialValue() {
                return new PacketPlayOutMapChunkBulk();
            }
        };

        // Don't include this loop because that would result in micro sending
        // if the size isn't close to the protocol max
        // keep it in the inside loop to reduce buffer pollution
        int minX = centX - viewDistance / 2;
        int maxX = centX + viewDistance / 2;
        for (int x = minX; x <= maxX; x += 1) {
            final int finalX = x;
            ThreadsHandler.worldExecutor().execute(() -> {
                int minZ = centZ - viewDistance / 2;
                int maxZ = centZ + viewDistance / 2;
                for (int z = minZ; z <= maxZ; z += 1) {
                    ChunkLocation location = ChunkLocation.create(finalX, z);
                    if (!knownChunks.add(location)) continue;

                    TridentChunk chunk = (TridentChunk) world().chunkAt(location, true);
                    PacketPlayOutChunkData data = chunk.asPacket();

                    PacketPlayOutMapChunkBulk b = bulk.get();
                    b.addEntry(data);

                    if (b.size() >= 1845152) { // send the packet if the length is close to the protocol maximum
                        connection.sendPacket(bulk.get());

                        bulk.set(new PacketPlayOutMapChunkBulk());
                    }

                    if (bulk.get().hasEntries()) {
                        connection.sendPacket(bulk.get());
                    }
                }
            });
        } */
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

        ProtocolMetadata meta = new ProtocolMetadata();
        encodeMetadata(meta);
        sendFiltered(new PacketPlayOutEntityMetadata().set("entityId", entityId()).set("metadata", meta),
                p -> !p.equals(this));
    }

    public boolean isCrouching() {
        return crouching;
    }

    @InternalUseOnly
    public void setCrouching(boolean crouching) {
        this.crouching = crouching;

        ProtocolMetadata meta = new ProtocolMetadata();
        encodeMetadata(meta);
        sendFiltered(new PacketPlayOutEntityMetadata().set("entityId", entityId()).set("metadata", meta),
                p -> !p.equals(this));
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
    public boolean connected() {
        return true;
    }

    @Override
    public Player asPlayer() {
        return this;
    }

    @Override
    public EntityType type() {
        return EntityType.PLAYER;
    }
}
