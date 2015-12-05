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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.tridentsdk.Trident;
import net.tridentsdk.base.BoundingBox;
import net.tridentsdk.base.Position;
import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.effect.sound.SoundEffect;
import net.tridentsdk.effect.sound.SoundEffectType;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.event.player.PlayerDisconnectEvent;
import net.tridentsdk.event.player.PlayerJoinEvent;
import net.tridentsdk.event.player.PlayerMoveEvent;
import net.tridentsdk.inventory.Item;
import net.tridentsdk.meta.ChatColor;
import net.tridentsdk.meta.MessageBuilder;
import net.tridentsdk.meta.block.Tile;
import net.tridentsdk.meta.nbt.CompoundTag;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.chunk.ChunkLocationSet;
import net.tridentsdk.server.concurrent.ThreadsHandler;
import net.tridentsdk.server.data.MetadataType;
import net.tridentsdk.server.data.ProtocolMetadata;
import net.tridentsdk.server.entity.TridentDroppedItem;
import net.tridentsdk.server.event.EventProcessor;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.packets.play.in.PacketPlayInPlayerClickWindow.ClickAction;
import net.tridentsdk.server.packets.play.out.*;
import net.tridentsdk.server.packets.play.out.PacketPlayOutChat.ChatPosition;
import net.tridentsdk.server.packets.play.out.PacketPlayOutPlayerListItem.PlayerListDataBuilder;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.util.Vector;
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

@ThreadSafe
public class TridentPlayer extends OfflinePlayer {
    private static final Map<UUID, Player> ONLINE_PLAYERS = new ConcurrentHashMap<>();
    private static final int MAX_VIEW = Trident.config().getInt("view-distance", 15);

    private final PlayerConnection connection;
    public final ChunkLocationSet knownChunks = new ChunkLocationSet(this);
    private final LinkedHashSet<Integer> dragSlots = new LinkedHashSet<>();
    private volatile ClickAction drag;
    private volatile boolean loggingIn = true;
    private volatile boolean sprinting;
    private volatile boolean crouching;
    private volatile boolean flying;
    private volatile byte skinFlags;
    private volatile Locale locale;
    private volatile int viewDistance = MAX_VIEW;
    private volatile Item pickedItem;
    private volatile String header;
    private volatile String footer;

    private TridentPlayer(UUID uuid, CompoundTag tag, TridentWorld world, ClientConnection connection) {
        super(uuid, tag, world);

        this.connection = PlayerConnection.createPlayerConnection(connection, this);
        inventory.sendTo(this);
    }

    public static void sendAll(Packet packet) {
        players().stream().forEach(p -> ((TridentPlayer) p).connection.sendPacket(packet));
    }

    public static void sendFiltered(Packet packet, Predicate<Player> predicate) {
        players().stream()
                .filter(predicate)
                .forEach(p -> ((TridentPlayer) p).connection.sendPacket(packet));
    }

    public static TridentPlayer spawnPlayer(ClientConnection connection, UUID id, String name) {
        // determine if this player has logged in before
        CompoundTag playerTag = OfflinePlayer.getOfflinePlayer(
                id) == null ? null : OfflinePlayer.getOfflinePlayer(id).asNbt();

        // if this player is new
        if (playerTag == null) {
            playerTag = OfflinePlayer.generatePlayer(id);
        }

        TridentPlayer p = new TridentPlayer(id, playerTag, TridentServer.WORLD, connection);
        p.executor = ThreadsHandler.playerExecutor();

        // fixeme ?? OfflinePlayer.OFFLINE_PLAYERS.put(id, p);
        ONLINE_PLAYERS.put(id, p);

        p.name = name;

        p.gameMode = GameMode.CREATIVE;//GameMode.of(((IntTag) playerTag.getTag("playerGameType")).value());

        p.executor.execute(() -> {
            p.connection.sendPacket(new PacketPlayOutJoinGame().set("entityId", p.entityId())
                    .set("gamemode", p.gameMode)
                    .set("dimension", p.world().settings().dimension())
                    .set("difficulty", p.world().settings().difficulty())
                    .set("maxPlayers", (short) Trident.config().getInt("max-players"))
                    .set("levelType", LevelType.DEFAULT));

            p.abilities.creative = 1;
            p.abilities.flySpeed = 0.135F;
            p.abilities.canFly = 1;

            p.spawnPosition = TridentServer.WORLD.spawnPosition();

            p.connection.sendPacket(PacketPlayOutPluginMessage.VANILLA_CHANNEL);
            p.connection.sendPacket(new PacketPlayOutServerDifficulty().set("difficulty", p.world().settings().difficulty()));
            p.connection.sendPacket(new PacketPlayOutSpawnPosition().set("location", p.spawnLocation()));
            p.connection.sendPacket(p.abilities.asPacket());
            p.connection.sendPacket(new PacketPlayOutPlayerCompleteMove().set("location",
                    p.spawnLocation()).set("flags", (byte) 0));

            sendAll(new PacketPlayOutPlayerListItem()
                    .set("action", 0)
                    .set("playerListData", new PlayerListDataBuilder[]{p.listData()}));

            List<PlayerListDataBuilder> builders = new ArrayList<>();

            players().stream().filter(player -> !player.equals(p))
                    .forEach(player -> builders.add(((TridentPlayer) player).listData()));
            TridentLogger.get().log(p.name + " has joined the server");

            p.connection.sendPacket(new PacketPlayOutPlayerListItem()
                    .set("action", 0)
                    .set("playerListData", builders.stream().toArray(value -> new PlayerListDataBuilder[value])));
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
        protocolMeta.setMeta(0, MetadataType.BYTE, (byte) ((fireTicks.intValue() == 0 ? 1 : 0) | (isCrouching() ? 2 : 0)
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

        knownChunks.update(7);
        connection.sendPacket(PacketPlayOutStatistics.DEFAULT_STATISTIC);

        // Wait for response
        for (Entity entity : world().entities()) {
            // Register mob, packet sent to new player
        }

        loggingIn = false;
        spawn();
        connection.sendPacket(new PacketPlayOutEntityVelocity()
                .set("entityId", entityId())
                .set("velocity", new Vector(0, -0.07, 0)));
        connection.sendPacket(new PacketPlayOutGameStateChange().set("reason", 3).set("value", (float) gameMode().asByte()));
        for (Tile tile : ((TridentWorld) world()).tilesInternal()) {
            tile.update(this);
        }

        EventProcessor.fire(new PlayerJoinEvent(this));

        MessageBuilder builder = new MessageBuilder(name + " has joined the server").color(ChatColor.YELLOW).build();
        for (Player player : players()) {
            TridentPlayer p = (TridentPlayer) player;
            builder.sendTo(player);

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
        if (!loggingIn) {
            ThreadsHandler.chunkExecutor().execute(() -> {
                knownChunks.clean(distance);
                knownChunks.update(distance);
            });
        }

        connection.tick();
    }

    @Override
    protected void doRemove() {
        knownChunks.clear();

        PacketPlayOutPlayerListItem item = new PacketPlayOutPlayerListItem();
        item.set("action", 4).set("playerListData", new PlayerListDataBuilder[]{
                new PlayerListDataBuilder().id(uniqueId).values(new Object[0])});
        sendAll(item);

        players().forEach(p ->
                new MessageBuilder(name + " has left the server").color(ChatColor.YELLOW).build().sendTo(p));
        TridentLogger.get().log(name + " has left the server");
        ONLINE_PLAYERS.remove(uniqueId());
        EventProcessor.fire(new PlayerDisconnectEvent(this));
    }

    @Override
    public void setPosition(Position loc) {
        double dX = loc.x() - position().x();
        double dY = loc.y() - position().y();
        double dZ = loc.z() - position().z();

        PlayerMoveEvent event = EventProcessor.fire(new PlayerMoveEvent(this, position(), loc));

        if (event.isIgnored()) {
            PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport();

            packet.set("entityId", entityId());
            packet.set("location", position());
            packet.set("onGround", onGround());

            connection.sendPacket(packet);
            return;
        }

        super.setPosition(loc);

        if(/* health() > 0 && */ gameMode() != GameMode.SPECTATE){
            BoundingBox checkBox = boundingBox().grow(1, 0.5, 1);
            ArrayList<Entity> items = position().world().getEntities(this, checkBox, entity -> entity instanceof TridentDroppedItem);
            items.stream().filter(item -> ((TridentDroppedItem) item).canPickupItem()).forEach(item -> {
                int started = ((TridentDroppedItem) item).item().quantity();
                window().putItem(((TridentDroppedItem) item).item());

                if(started > ((TridentDroppedItem) item).item().quantity()){
                    SoundEffect soundEffect = loc.world().playSound(SoundEffectType.RANDOM_POP);
                    soundEffect.setPosition(position().asVector());
                    soundEffect.apply(this);
                }

                if(((TridentDroppedItem) item).item().quantity() <= 0){
                    PacketPlayOutCollectItem collectItem = new PacketPlayOutCollectItem();
                    collectItem.set("collectedId", item.entityId());
                    collectItem.set("collectorId", entityId());
                    sendAll(collectItem);
                    item.remove();
                }
            });

            if (!items.isEmpty()) {
                window().sendTo(this);
            }
        }

        // fixme floating point comparison
        if (dX == 0 && dY == 0 && dZ == 0) {
            sendFiltered(new PacketPlayOutEntityLook().set("entityId", entityId())
                            .set("location", loc).set("onGround", onGround), player -> !player.equals(this)
                    );

            return;
        }

        if (dX > 4 || dY > 4 || dZ > 4 || (ticksExisted.get() & 1) == 0) {
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
        TridentLogger.get().log(name + " was kicked for " + reason);
    }

    private static final Map<UUID, String> textures = new ConcurrentHashMap<>();
    public PlayerListDataBuilder listData() {
        String[] texture = texture().split("#");
        return new PlayerListDataBuilder()
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
                if (object.isJsonNull()) {
                    return " # ";
                }

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
        return connection;
    }

    public static final int SLOT_OFFSET = 36;

    public void setSlot(short slot) {
        if ((int) slot > 8 || (int) slot < 0) {
            TridentLogger.get().error(new IllegalArgumentException("Slot must be within the ranges of 0-8"));
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
    public void sendRaw(String... messages) {
        Stream.of(messages)
                .filter(m -> m != null)
                .forEach(message -> connection.sendPacket(new PacketPlayOutChat()
                        .set("jsonMessage", message)
                        .set("position", ChatPosition.CHAT)));
    }

    @Override
    public void setGameMode(GameMode mode) {
        super.setGameMode(mode);

        connection.sendPacket(abilities.asPacket());
    }

    public boolean isFlying() {
        return flying;
    }

    public void setFlying(boolean flying) {
        this.flying = flying;

        abilities.flying = flying ? (byte) 1 : (byte) 0;
        connection.sendPacket(abilities.asPacket());
    }

    public boolean isFlyMode() {
        return abilities.canFly();
    }

    public void setFlyMode(boolean flying) {
        abilities.canFly = flying ? (byte) 1 : (byte) 0;
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

    @Override
    public Item pickedItem() {
        return pickedItem;
    }

    @Override
    public void setPickedItem(Item item) {
        pickedItem = item;
    }

    @Override
    public String header() {
        return header;
    }

    @Override
    public void setHeader(MessageBuilder builder) {
        if (!builder.isBuilt()) {
            builder.build();
        }

        header = builder.asJson();
        connection.sendPacket(new PacketPlayOutPlayerListUpdate()
                .set("header", header)
                .set("footer", footer == null ? "{\"text\": \"\"}" : footer));
    }

    @Override
    public String footer() {
        return footer;
    }

    @Override
    public void setFooter(MessageBuilder builder) {
        if (!builder.isBuilt()) {
            builder.build();
        }

        footer = builder.asJson();
        connection.sendPacket(new PacketPlayOutPlayerListUpdate()
                .set("header", header == null ? "{\"text\": \"\"}" : header)
                .set("footer", footer));
    }

    public LinkedHashSet<Integer> dragSlots() {
        return dragSlots;
    }

    public ClickAction drag() {
        return drag;
    }

    public void setDrag(ClickAction drag) {
        this.drag = drag;
    }
}