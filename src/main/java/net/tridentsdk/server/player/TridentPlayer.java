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
import net.tridentsdk.command.CmdSourceType;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.event.player.PlayerJoinEvent;
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
import net.tridentsdk.server.world.TridentChunk;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.ui.bossbar.BossBar;
import net.tridentsdk.ui.chat.ChatColor;
import net.tridentsdk.ui.chat.ChatComponent;
import net.tridentsdk.ui.chat.ChatType;
import net.tridentsdk.ui.chat.ClientChatMode;
import net.tridentsdk.ui.tablist.TabList;
import net.tridentsdk.ui.title.Title;
import net.tridentsdk.world.opt.GameMode;

import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * This class is the implementation of a Minecraft client
 * that is represented by a physical entity in a world.
 */
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
    private static final ConcurrentSkipListMap<String, Player> playerNames =
            new ConcurrentSkipListMap<>((c0, c1) -> {
                // Indexes: 0123456789AaBbCcDdEeFfGg..._
                for (int i = 0; i < c0.length() && i < c1.length(); i++) {
                    char c0i = c0.charAt(i);
                    char c1i = c1.charAt(i);

                    boolean d0 = Character.isDigit(c0i) || c0i == '_';
                    boolean d1 = Character.isDigit(c1i) || c0i == '_';

                    boolean u0 = Character.isUpperCase(c0i);
                    boolean u1 = Character.isUpperCase(c1i);

                    int t0 = (Character.toUpperCase(c0i) - 'A' << 1) + (u0 ? 0 : 1);
                    int t1 = (Character.toUpperCase(c1i) - 'A' << 1) + (u1 ? 0 : 1);

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

                    if (t0 > t1) { // If letter is ahead, c0 is after
                        return 1;
                    } else if (t1 > t0) { // If letter is behind, c0 is before
                        return -1;
                    }
                }

                if (c0.length() > c1.length()) { // If all chars equal, shorter goes first
                    return 1;
                } else if (c0.length() < c1.length()) {
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
    private volatile ChatComponent displayName;
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
    private final Set<TridentChunk> heldChunks = Collections.newSetFromMap(new ConcurrentHashMap<>());

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
    @Getter
    private volatile TabList tabList;
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
        this.displayName = ChatComponent.text(name);
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
     */
    public static TridentPlayer spawn(NetClient client, String name, UUID uuid,
                                      TabListElement.PlayerProperty skinTextures) {
        TridentWorld world = TridentServer.getInstance().getWorldLoader().getDefaultWorld();
        TridentPlayer player = new TridentPlayer(client, world, name, uuid, skinTextures);
        client.setPlayer(player);

        TridentPlayer.players.put(uuid, player);
        Login.finish();
        TridentPlayer.playerNames.put(name, player);

        player.updateChunks();
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

        this.client.sendPacket(new PlayOutJoinGame(this, this.getWorld()));
        this.client.sendPacket(PlayOutPluginMsg.BRAND);
        TridentPluginChannel.autoAdd(this);
        this.client.sendPacket(new PlayOutDifficulty(this.getWorld()));
        this.client.sendPacket(new PlayOutSpawnPos());
        this.client.sendPacket(new PlayOutPlayerAbilities(this));
        this.inventory.update();
        this.client.sendPacket(new PlayOutPosLook(this));

        this.setTabList(TridentGlobalTabList.getInstance());
        TridentGlobalTabList.getInstance().update();

        // TODO default permissions?
        Collections.addAll(this.permissions, "minecraft.help");
        if (TridentServer.getInstance().getOpsList().getOps().contains(this.uuid)) {
            this.op = true;
        }

        ChatComponent chat = ChatComponent.create().setColor(ChatColor.YELLOW).
                setTranslate("multiplayer.player.joined").
                addWith(this.name);
        RecipientSelector.whoCanSee(this, true, new PlayOutSpawnPlayer(this));
        this.getWorld().getOccupants().forEach(p -> p.sendMessage(chat, ChatType.CHAT));

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() ->
                TridentServer.getInstance().getEventController().dispatch(new PlayerJoinEvent(this)));

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
        TridentGlobalTabList.getInstance().update();
        TridentInventory.clean();
        for (TridentChunk chunk : this.heldChunks) {
            chunk.getHolders().remove(this);
        }
        this.heldChunks.clear();

        ChatComponent chat = ChatComponent.create().setColor(ChatColor.YELLOW)
                .setTranslate("multiplayer.player.left")
                .addWith(this.name);
        this.getWorld().getOccupants().forEach(e -> e.sendMessage(chat, ChatType.CHAT));
        this.client.disconnect(ChatComponent.empty());
    }

    @Override
    public void setDisplayName(ChatComponent displayName) {
        this.displayName = displayName != null ? displayName : ChatComponent.text(this.name);
        // TODO update
    }

    @Override
    public void sendMessage(ChatComponent chat, ChatType type) {
        ClientChatMode chatMode = this.chatMode;
        if (chatMode == ClientChatMode.COMMANDS_ONLY && type == ChatType.SYSTEM
                || chatMode == ClientChatMode.CHAT_AND_COMMANDS) {
            this.net().sendPacket(new PlayOutChat(chat, type, this.chatColors));
        }
    }

    @Override
    public void kick(ChatComponent reason) {
        this.client.disconnect(reason);
    }

    @Override
    public void setTabList(TabList tabList) {
        TabList old = this.tabList;
        if (old != null) {
            // TODO does this need send remove elements packet?
            old.unsubscribe(this);
        }

        if (tabList != null) {
            this.tabList = tabList;
            tabList.subscribe(this);
            ((TridentTabList) tabList).forceSend(this);
        }
    }

    @Override
    public List<BossBar> getBossBars() {
        return Collections.unmodifiableList(this.bossBars);
    }

    @Override
    public void addBossBar(BossBar bossBar) {
        if(bossBar == null){
            throw new NullPointerException();
        }
        
        if (this.bossBars.add(bossBar)) {
            this.net().sendPacket(new PlayOutBossBar.Add(bossBar));
        }
    }

    @Override
    public void removeBossBar(BossBar bossBar) {
        if(bossBar == null){
            throw new NullPointerException();
        }
        
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

            int changed = AbstractBossBar.STATE.get(bossBar);
            do {
                boolean sky = (changed >>> 4 & 1) == 1;
                boolean title = (changed >>> 3 & 1) == 1;
                boolean health = (changed >>> 2 & 1) == 1;
                boolean style = (changed >>> 1 & 1) == 1;
                boolean flags = (changed & 1) == 1;

                if (sky) {
                    this.net().sendPacket(new PlayOutBossBar.Add(bossBar));
                } else {
                    if (health) {
                        this.net().sendPacket(new PlayOutBossBar.UpdateHealth(bossBar));
                    }
                    if (title) {
                        this.net().sendPacket(new PlayOutBossBar.UpdateTitle(bossBar));
                    }
                    if (style) {
                        this.net().sendPacket(new PlayOutBossBar.UpdateStyle(bossBar));
                    }
                    if (flags) {
                        this.net().sendPacket(new PlayOutBossBar.UpdateFlags(bossBar));
                    }
                }
            } while (!bossBar.unsetChanged(changed));
        }
    }

    @Override
    public void sendTitle(Title title) {
        if (!title.isDefaultFadeTimes()) {
            this.net().sendPacket(new PlayOutTitle.SetTiming(title));
        }

        ChatComponent mainTitle = title.getHeader();
        ChatComponent subtitle = title.getSubtitle();

        this.net().sendPacket(new PlayOutTitle.SetSubtitle(subtitle));
        this.net().sendPacket(new PlayOutTitle.SetTitle(mainTitle));
    }

    @Override
    public void resetTitle() {
        this.net().sendPacket(new PlayOutTitle.Reset());
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
        // TODO Push update to tablist and other players
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
    public void updateChunks() {
        // TODO Improve this algorithm
        TridentWorld world = (TridentWorld) this.getPosition().getWorld();
        int centerX = this.getPosition().getChunkX();
        int centerZ = this.getPosition().getChunkZ();

        int radius = this.renderDistance;

        this.pool.execute(() -> {
            for (int x = centerX - radius; x < centerX + radius; x++) {
                for (int z = centerZ - radius; z < centerZ + radius; z++) {
                    TridentChunk chunk = world.getChunkAt(x, z);
                    if (!this.heldChunks.contains(chunk)) {
                        this.heldChunks.add(chunk);
                        chunk.getEntities().filter(p -> !p.equals(this)).forEach(e -> this.net().sendPacket(((TridentEntity) e).getSpawnPacket()));
                        chunk.getHolders().add(this);
                        this.net().sendPacket(new PlayOutChunk(chunk));
                    }
                }
            }
        });

        this.pool.execute(() -> {
            for (TridentChunk chunk : this.heldChunks) {
                if (Math.abs(chunk.getX() - centerX) > radius || Math.abs(chunk.getZ() - centerZ) > radius) {
                    this.heldChunks.remove(chunk);
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
    public void runCommand(String command) {
        TridentServer.getInstance().getLogger().log(this.name + " issued server command: /" + command);
        try {
            if (!ServerThreadPool.forSpec(PoolSpec.PLUGINS)
                    .submit(() -> TridentServer.getInstance().getCmdHandler().dispatch(command, this)).get()) {
                this.sendMessage(ChatComponent.create().setColor(ChatColor.RED).setText("No command found for " +
                        command.split(" ")[0]));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CmdSourceType getCmdType() {
        return CmdSourceType.PLAYER;
    }

    @Override
    public boolean hasPerm(String perm) {
        return this.op || this.permissions.contains(perm);
    }

    @Override
    public void addPerm(String perm) {
        this.permissions.add(perm);
    }

    @Override
    public boolean removePerm(String perm) {
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