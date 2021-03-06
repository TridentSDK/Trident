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

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.tridentsdk.base.Position;
import net.tridentsdk.command.CommandSourceType;
import net.tridentsdk.doc.Policy;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.event.player.PlayerChatEvent;
import net.tridentsdk.event.player.PlayerJoinEvent;
import net.tridentsdk.event.player.PlayerQuitEvent;
import net.tridentsdk.inventory.Inventory;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.concurrent.PoolSpec;
import net.tridentsdk.server.concurrent.ServerThreadPool;
import net.tridentsdk.server.entity.TridentEntity;
import net.tridentsdk.server.entity.meta.EntityMetaType;
import net.tridentsdk.server.inventory.TridentInventory;
import net.tridentsdk.server.inventory.TridentPlayerInventory;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.PacketOut;
import net.tridentsdk.server.packet.login.Login;
import net.tridentsdk.server.packet.play.*;
import net.tridentsdk.server.plugin.TridentPluginChannel;
import net.tridentsdk.server.ui.bossbar.AbstractBossBar;
import net.tridentsdk.server.ui.tablist.TabListElement;
import net.tridentsdk.server.ui.tablist.TridentGlobalTabList;
import net.tridentsdk.server.ui.tablist.TridentTabList;
import net.tridentsdk.server.util.Debug;
import net.tridentsdk.server.world.TridentChunk;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.ui.bossbar.BossBar;
import net.tridentsdk.ui.chat.*;
import net.tridentsdk.ui.tablist.TabList;
import net.tridentsdk.ui.title.Title;
import net.tridentsdk.world.IntPair;
import net.tridentsdk.world.opt.GameMode;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * This class is the implementation of a Minecraft client
 * that is represented by a physical entity in a world.
 */
@ToString(of = "name")
@ThreadSafe
@EntityMetaType(TridentPlayerMeta.class)
public class TridentPlayer extends TridentEntity implements Player {
    /**
     * The players on the server
     */
    @Getter
    private static final Map<UUID, TridentPlayer> players = new ConcurrentHashMap<>();
    /**
     * The players ordered by name
     */
    @Getter
    private static final ConcurrentSkipListMap<String, TridentPlayer> playerNames =
            new ConcurrentSkipListMap<>((c0, c1) -> {
                int l0 = c0.length();
                int l1 = c1.length();

                if (l0 == 0 && l1 > 0) {
                    return -1;
                } else if (l1 == 0 && l0 > 0) {
                    return 1;
                }

                // Indexes: 0123456789AaBbCcDdEeFfGg..._
                for (int i = 0, j = Math.min(l0, l1); i < j; i++) {
                    char c0i = c0.charAt(i);
                    char c1i = c1.charAt(i);

                    boolean d0 = Character.isDigit(c0i) || c0i == '_';
                    boolean d1 = Character.isDigit(c1i) || c0i == '_';

                    if (d0 && d1) { // Compare if both are digits
                        int cmp = Character.compare(c0i, c1i);
                        if (cmp != 0) { // If they aren't equal, return comparison
                            return cmp;
                        }
                    } else if (d0) { // If only 0 is digit, c0 is before
                        return -1;
                    } else if (d1) { // If only 1 is digit, c0 is after
                        return 1;
                    }

                    boolean u0 = Character.isUpperCase(c0i);
                    boolean u1 = Character.isUpperCase(c1i);

                    char r0 = u0 ? 'A' : 'a';
                    char r1 = u1 ? 'A' : 'a';

                    int t0 = (c0i - r0 << 1) + (u0 ? 0 : 1);
                    int t1 = (c1i - r1 << 1) + (u1 ? 0 : 1);

                    if (t0 > t1) { // If letter is ahead, c0 is after
                        return 1;
                    } else if (t1 > t0) { // If letter is behind, c0 is before
                        return -1;
                    }
                }

                if (l0 > l1) { // If all chars equal, shorter goes first
                    return 1;
                } else if (l0 < l1) {
                    return -1;
                }

                return 0; // Otherwise same
            });

    // -----------------------------------------------------
    // LOGIN HEADERS ---------------------------------------
    // -----------------------------------------------------

    /**
     * The net connection that this player has to the
     * server
     */
    private final NetClient client;
    /**
     * The player's name
     */
    @Getter
    private final String name;
    /**
     * The player's UUID
     */
    @Getter
    private final UUID uuid;
    /**
     * The player's display name
     */
    @Getter
    private volatile ChatComponent tabListName;
    /**
     * The player's current game mode
     */
    @Getter
    private volatile GameMode gameMode;
    /**
     * The player's skin textures.
     */
    @Getter
    private volatile TabListElement.PlayerProperty skinTextures;
    /**
     * Whether the player has finished logging in
     */
    private final AtomicBoolean finishedLogin = new AtomicBoolean(false);

    // -----------------------------------------------------
    // CHUNKS ----------------------------------------------
    // -----------------------------------------------------

    /**
     * The player's render distance
     */
    @Getter
    @Setter
    private volatile int renderDistance = 7;
    /**
     * The chunks that are held by this player
     */
    private final Map<IntPair, TridentChunk> heldChunks = new ConcurrentHashMap<>();

    // -----------------------------------------------------
    // PLAYER META -----------------------------------------
    // -----------------------------------------------------

    /**
     * The player's meta data
     */
    @Getter
    private final TridentPlayerMeta metadata;
    /**
     * Whether the player is in god mode
     */
    @Getter
    private volatile boolean godMode;
    /**
     * Whether the player can fly
     */
    private volatile boolean canFly;
    /**
     * Whether the player is flying
     */
    @Getter
    private volatile boolean flying;
    /**
     * The player's flying speed
     */
    @Getter
    private volatile float flyingSpeed = Player.DEFAULT_FLYING_SPEED;
    /**
     * The player's walking speed
     */
    @Getter
    private volatile float walkingSpeed = Player.DEFAULT_WALKING_SPEED;

    // -----------------------------------------------------
    // UI SETTINGS -----------------------------------------
    // -----------------------------------------------------

    /**
     * The player's current tablist
     */
    @GuardedBy("heldChunks") // random field used to lock
    @Getter
    private TridentTabList tabList;
    /**
     * The boss bars that are being displayed to this
     * player.
     */
    private final List<BossBar> bossBars = new CopyOnWriteArrayList<>();
    /**
     * The player's language locale
     */
    @Getter
    @Setter
    private volatile String locale;
    /**
     * Whether or not this client allows chat colors
     */
    @Setter
    private volatile boolean chatColors;
    /**
     * The allowed chat filters for this client
     */
    @Setter
    private volatile ClientChatMode chatMode;
    /**
     * The player's inventory
     */
    @Getter
    private final TridentPlayerInventory inventory;

    // -----------------------------------------------------
    // PERMISSIONS -----------------------------------------
    // -----------------------------------------------------

    /**
     * The collection of permissions held by this player
     */
    private final Set<String> permissions = Collections.newSetFromMap(new ConcurrentHashMap<>());
    /**
     * Whether or not this player is an operator
     */
    @Getter
    private volatile boolean op;

    /**
     * Constructs a new player.
     */
    private TridentPlayer(NetClient client, TridentWorld world, String name, UUID uuid,
                          TabListElement.PlayerProperty skinTextures) {
        super(world, PoolSpec.PLAYERS);
        this.metadata = (TridentPlayerMeta) super.getMetadata();

        this.client = client;
        this.name = name;
        this.uuid = uuid;
        this.tabListName = ChatComponent.text(name);
        this.gameMode = world.getWorldOptions().getGameMode();
        this.canFly = this.gameMode == GameMode.CREATIVE || this.gameMode == GameMode.SPECTATOR;
        this.skinTextures = skinTextures;
        this.inventory = new TridentPlayerInventory(client);
    }

    /**
     * Spawns a new player.
     *
     * @param client the client representing the player
     * @param name the player name
     * @param uuid the player UUID
     * @param skinTextures the player textures
     * @return the spawned player
     */
    public static TridentPlayer spawn(NetClient client, String name, UUID uuid,
                                      TabListElement.PlayerProperty skinTextures) {
        TridentWorld world = TridentServer.getInstance().getWorldLoader().getDefaultWorld();
        TridentPlayer player = new TridentPlayer(client, world, name, uuid, skinTextures);
        client.setPlayer(player);

        TridentPlayer.players.put(uuid, player);
        TridentPlayer.playerNames.put(name, player);
        Login.finish();

        player.updateChunks(player.getPosition());
        player.resumeLogin();

        return player;
    }

    /**
     * Resumes the joining process after the player has
     * confirmed the client spawn position.
     */
    public void resumeLogin() {
        if (!this.finishedLogin.compareAndSet(false, true)) {
            return;
        }

        TridentWorld world = this.getWorld();
        this.client.sendPacket(new PlayOutJoinGame(this, world));
        this.client.sendPacket(PlayOutPluginMsg.BRAND);
        TridentPluginChannel.autoAdd(this);
        this.client.sendPacket(new PlayOutDifficulty(world));
        this.client.sendPacket(new PlayOutSpawnPos());
        this.client.sendPacket(new PlayOutPlayerAbilities(this));
        this.inventory.update();
        this.client.sendPacket(new PlayOutPosLook(this));

        this.client.sendPacket(new PlayOutTime(world.getAge().longValue(), world.getTime()));
        if (world.getWeather().isRaining()) {
            this.client.sendPacket(new PlayOutGameState(2, 0));
        }

        this.setTabList(TridentGlobalTabList.getInstance());

        // TODO default permissions?
        Collections.addAll(this.permissions, "minecraft.help");
        if (TridentServer.getInstance().getOpsList().getOps().contains(this.uuid)) {
            this.op = true;
        }
        if (Debug.IS_DEBUGGING) {
            this.permissions.add("trident.debug");
        }

        RecipientSelector.whoCanSee(this, true, new PlayOutSpawnPlayer(this));

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            ChatComponent chat = ChatComponent.create()
                    .setColor(ChatColor.YELLOW)
                    .setTranslate("multiplayer.player.joined")
                    .addWith(this.name);
            PlayerJoinEvent event = new PlayerJoinEvent(this, chat);
            TridentServer.getInstance().getEventController().dispatch(event);
            ChatComponent message = event.getMessage();
            if (message != null)
                players.values().forEach(p -> p.sendMessage(message, ChatType.CHAT));
        });

        TridentServer.getInstance().getLogger().log("Player " + this.name + " [" + this.uuid + "] has connected");
    }

    /**
     * Obtains the network connection of this player.
     *
     * @return the net connection
     */
    public NetClient net() {
        return this.client;
    }

    @Override
    public void doTick() {
        this.client.tick();
    }

    @Override
    public PacketOut getSpawnPacket() {
        // TODO send tablist to update skin??
        return new PlayOutSpawnPlayer(this);
    }

    @Override
    public void doRemove() {
        // If the player isn't in the list, they haven't
        // finished logging in yet; cleanup
        if (TridentPlayer.players.remove(this.uuid) == null) {
            Login.finish();
        }

        TridentPluginChannel.autoRemove(this);
        playerNames.remove(this.name);

        this.setTabList(null);
        TridentGlobalTabList.getInstance().unsubscribe(this);
        TridentInventory.clean();
        for (TridentChunk chunk : this.heldChunks.values()) {
            chunk.getHolders().remove(this);
        }
        this.heldChunks.clear();

        ChatComponent chat = ChatComponent.create()
                .setColor(ChatColor.YELLOW)
                .setTranslate("multiplayer.player.left")
                .addWith(this.name);
        TridentServer.getInstance().getEventController().dispatch(new PlayerQuitEvent(this, chat), e -> {
            ChatComponent message = e.getMessage();
            if (message != null)
                players.values().forEach(p -> p.sendMessage(message, ChatType.CHAT));
        });
        this.client.disconnect(ChatComponent.empty());
    }

    @Override
    public void setTabListName(ChatComponent name) {
        if (name != null && name.getText() == null)
            throw new IllegalArgumentException("display name must set text field");
        this.tabListName = name != null ? name : ChatComponent.text(this.name);

        TridentGlobalTabList.getInstance().updateTabListName(this);
    }

    @Override
    public void sendMessage(ChatComponent chat, ChatType type) {
        ClientChatMode chatMode = this.chatMode;
        if (chatMode == ClientChatMode.COMMANDS_ONLY && type == ChatType.SYSTEM ||
                chatMode == ClientChatMode.CHAT_AND_COMMANDS) {
            this.net().sendPacket(new PlayOutChat(chat, type, this.chatColors));
        }
    }

    @Override
    public void kick(ChatComponent reason) {
        this.client.disconnect(reason);
    }

    @Override
    public void setTabList(TabList tabList) {
        synchronized (this.heldChunks) {
            TridentTabList old = this.tabList;
            if (old != null) {
                old.unsubscribe(this);
            }

            if (tabList != null) {
                this.tabList = (TridentTabList) tabList;
                this.tabList.subscribe(this);
            }
        }
    }

    @Override
    public List<BossBar> getBossBars() {
        return Collections.unmodifiableList(this.bossBars);
    }

    @Override
    public void addBossBar(BossBar bossBar) {
        Objects.requireNonNull(bossBar, "boss bar cannot be null");
        
        if (this.bossBars.add(bossBar)) {
            this.net().sendPacket(new PlayOutBossBar.Add(bossBar));
        }
    }

    @Override
    public void removeBossBar(BossBar bossBar) {
        Objects.requireNonNull(bossBar, "boss bar cannot be null");
        
        if (this.bossBars.remove(bossBar)) {
            this.net().sendPacket(new PlayOutBossBar.Remove(bossBar));
        }
    }

    @Override
    public void updateBossBars() {
        this.updateBossBars(false);
    }

    private void updateBossBars(boolean force) {
        for (BossBar bar : this.bossBars) {
            AbstractBossBar bossBar = (AbstractBossBar) bar;
            if (force) {
                this.net().sendPacket(new PlayOutBossBar.Add(bossBar));
                continue;
            }

            this.net().sendPacket(new PlayOutBossBar.UpdateHealth(bossBar));
            this.net().sendPacket(new PlayOutBossBar.UpdateTitle(bossBar));
            this.net().sendPacket(new PlayOutBossBar.UpdateStyle(bossBar));
            this.net().sendPacket(new PlayOutBossBar.UpdateFlags(bossBar));
        }
    }

    @Override
    public void sendTitle(Title title) {
        synchronized (this.bossBars) { // this.bossBars simply used as lock object for titles
            if (!title.isDefaultFadeTimes()) {
                this.net().sendPacket(new PlayOutTitle.SetTiming(title));
            }

            ChatComponent mainTitle = title.getHeader();
            ChatComponent subtitle = title.getSubtitle();

            this.net().sendPacket(new PlayOutTitle.SetSubtitle(subtitle));
            this.net().sendPacket(new PlayOutTitle.SetTitle(mainTitle));
        }
    }

    @Override
    public void resetTitle() {
        synchronized (this.bossBars) {
            this.net().sendPacket(new PlayOutTitle.Reset());
        }
    }

    @Override
    public void openInventory(Inventory inventory) {
        TridentInventory.open((TridentInventory) inventory, this);
    }

    /**
     * Sets the texture of this player to a different skin
     * data.
     *
     * @param skinTextures the skin textures
     */
    public void setTextures(TabListElement.PlayerProperty skinTextures) {
        this.skinTextures = skinTextures;

        TridentGlobalTabList.getInstance().update(this);
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
        this.canFly = gameMode == GameMode.CREATIVE || gameMode == GameMode.SPECTATOR;
        this.client.sendPacket(new PlayOutPlayerAbilities(this));
        this.client.sendPacket(new PlayOutGameState(3, gameMode.asInt()));
    }

    @Override
    public void setGodMode(boolean godMode) {
        this.setGodMode(godMode, true);
    }

    public void setGodMode(boolean godMode, boolean sendPacket) {
        this.godMode = godMode;
        if (sendPacket) {
            this.client.sendPacket(new PlayOutPlayerAbilities(this));
        }
    }

    @Override
    public boolean canFly() {
        return this.canFly;
    }

    @Override
    public void setCanFly(boolean canFly) {
        this.setCanFly(canFly, true);
    }

    public void setCanFly(boolean canFly, boolean sendPacket) {
        this.canFly = canFly;
        if (sendPacket) {
            this.client.sendPacket(new PlayOutPlayerAbilities(this));
        }
    }

    @Override
    public void setFlying(boolean flying) {
        this.setFlying(flying, true);
    }

    public void setFlying(boolean flying, boolean sendPacket) {
        this.flying = flying;
        if (sendPacket) {
            this.client.sendPacket(new PlayOutPlayerAbilities(this));
        }
    }

    @Override
    public void setFlyingSpeed(float flyingSpeed) {
        this.setFlyingSpeed(flyingSpeed, true);
    }

    public void setFlyingSpeed(float flyingSpeed, boolean sendPacket) {
        this.flyingSpeed = flyingSpeed;
        if (sendPacket) {
            this.client.sendPacket(new PlayOutPlayerAbilities(this));
        }
    }

    @Override
    public void setWalkingSpeed(float walkingSpeed) {
        this.setWalkingSpeed(walkingSpeed, true);
    }

    @Override
    public void setSprinting(boolean sprinting) {
        this.metadata.setSprinting(sprinting);
        if (sprinting) {
            this.walkingSpeed = Player.DEFAULT_SPRINT_SPEED;
        } else {
            this.walkingSpeed = Player.DEFAULT_WALKING_SPEED;
        }
        this.client.sendPacket(new PlayOutPlayerAbilities(this));
        this.updateMetadata();
    }

    @Override
    public boolean isSprinting() {
        return this.metadata.isSprinting();
    }

    @Override
    public void setCrouching(boolean crouching) {
        this.metadata.setCrouched(crouching);
        this.updateMetadata();
    }

    @Override
    public boolean isCrouching() {
        return this.metadata.isCrouched();
    }

    public void setWalkingSpeed(float walkingSpeed, boolean sendPacket) {
        this.walkingSpeed = walkingSpeed;
        if (sendPacket) {
            this.client.sendPacket(new PlayOutPlayerAbilities(this));
        }
    }

    /**
     * Send an update to the client with the chunks
     * If direction is null, chunks around the player will be sent
     */
    public void updateChunks(Position position) {
        TridentWorld world = (TridentWorld) position.getWorld();
        int centerX = position.getChunkX();
        int centerZ = position.getChunkZ();

        int radius = this.renderDistance;

        this.pool.execute(() -> {
            for (int x = centerX - radius; x < centerX + radius; x++) {
                for (int z = centerZ - radius; z < centerZ + radius; z++) {
                    IntPair pair = IntPair.make(x, z);
                    if (!this.heldChunks.containsKey(pair)) {
                        TridentChunk chunk = world.getChunkAt(x, z);
                        this.heldChunks.put(pair, chunk);
                        chunk.getHolders().add(this);
                        chunk.getEntities().filter(e -> !e.equals(this)).forEach(e -> this.net().sendPacket(((TridentEntity) e).getSpawnPacket()));
                        this.net().sendPacket(new PlayOutChunk(chunk));
                    }
                }
            }
        });

        this.pool.execute(() -> {
            for (TridentChunk chunk : this.heldChunks.values()) {
                if (Math.abs(chunk.getX() - centerX) > radius || Math.abs(chunk.getZ() - centerZ) > radius) {
                    this.heldChunks.remove(IntPair.make(chunk.getX(), chunk.getZ()));
                    chunk.getHolders().remove(this);
                    this.net().sendPacket(new PlayOutUnloadChunk(chunk.getX(), chunk.getZ()));

                    if (!chunk.getEntitySet().isEmpty() || !chunk.getOccupants().isEmpty()) {
                        this.net().sendPacket(new PlayOutDestroyEntities(chunk.getEntities().collect(Collectors.toList())));
                    }
                    chunk.checkValidForGc();
                }
            }
        });
    }

    @Override
    public void chat(String msg) {
        ChatComponent chat = ChatComponent.create()
                .setTranslate("chat.type.text")
                .addWith(ChatComponent.create()
                        .setText(this.getName())
                        .setClickEvent(ClickEvent.of(ClickAction.SUGGEST_COMMAND, "/tell " + this.getName() + " ")))
                .addWith(msg);
        Collection<Player> recipients = new ArrayList<>(TridentPlayer.getPlayers().values());
        PlayerChatEvent _event = new PlayerChatEvent(this, chat, recipients);
        TridentServer.getInstance().getEventController().dispatch(_event, event -> {
            if (!event.isCancelled()) {
                ChatComponent chatComponent = event.getChatComponent();
                event.getRecipients().forEach(p -> p.sendMessage(chatComponent, ChatType.CHAT));
            }
            TridentServer.getInstance().getLogger().log(getName() + " [" + getUuid() + "]: " + msg);
        });
    }

    @Override
    @Policy("plugin thread only")
    public void runCommand(String command) {
        TridentServer.getInstance().getLogger().log(this.name + " issued server command: /" + command);
        if (!TridentServer.getInstance().getCommandHandler().dispatch(command, this)) {
            this.sendMessage(ChatComponent.create().setColor(ChatColor.RED).setText("No command found for " + command.split(" ")[0]));
        }
    }

    @Override
    public CommandSourceType getCmdType() {
        return CommandSourceType.PLAYER;
    }

    @Override
    public boolean hasPermission(String perm) {
        return this.op || this.permissions.contains(perm);
    }

    @Override
    public void addPermission(String perm) {
        this.permissions.add(perm);
    }

    @Override
    public boolean removePermission(String perm) {
        return this.permissions.remove(perm);
    }

    @Override
    public void setOp(boolean op) {
        this.op = op;

        if (op) {
            TridentServer.getInstance().getOpsList().addOp(this.uuid);

            ChatComponent c = ChatComponent.
                    create().
                    setColor(ChatColor.GRAY).
                    setText("[Server: " + this.name + " has been opped]");
            for (UUID uuid : TridentServer.getInstance().getOpsList().getOps()) {
                TridentPlayer p = players.get(uuid);
                if (p != null) {
                    p.sendMessage(c);
                }
            }
        } else {
            TridentServer.getInstance().getOpsList().removeOp(this.uuid);

            ChatComponent c = ChatComponent.
                    create().
                    setColor(ChatColor.GRAY).
                    setText("[Server " + this.name + " has been deopped]");
            for (UUID uuid : TridentServer.getInstance().getOpsList().getOps()) {
                TridentPlayer p = players.get(uuid);
                if (p != null) {
                    p.sendMessage(c);
                }
            }
        }
    }
}