/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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

import com.google.common.collect.Maps;
import net.tridentsdk.base.Position;
import net.tridentsdk.chat.ChatColor;
import net.tridentsdk.chat.ChatComponent;
import net.tridentsdk.chat.ChatType;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.entity.TridentEntity;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.play.*;
import net.tridentsdk.server.ui.tablist.TridentTabList;
import net.tridentsdk.server.ui.tablist.TridentTabListManager;
import net.tridentsdk.server.world.TridentChunk;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.ui.tablist.TabList;
import net.tridentsdk.world.World;
import net.tridentsdk.world.WorldLoader;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Map;
import java.util.UUID;

/**
 * This class is the implementation of a Minecraft client
 * that is represented by a physical entity in a world.
 */
@ThreadSafe
public class TridentPlayer extends TridentEntity implements Player {
    // TODO player abilities
    // TODO client settings
    // TODO chunks

    /**
     * The players on the server
     */
    public static final Map<UUID, TridentPlayer> PLAYERS = Maps.newConcurrentMap();
    /**
     * The net connection that this player has to the
     * server
     */
    private final NetClient client;
    /**
     * The player's name
     */
    private final String name;
    /**
     * The player's UUID
     */
    private final UUID uuid;

    private TabList tabList;
    private String textures;

    /**
     * Constructs a new player.
     */
    private TridentPlayer(NetClient client, World world, String name, UUID uuid) {
        super(world);
        this.client = client;
        this.name = name;
        this.uuid = uuid;

        setTabList(TridentTabListManager.getInstance().getGlobalTabList());
    }

    /**
     * Spawns a new player.
     *
     * @param client the client representing the player
     * @param name the player name
     * @param uuid the player UUID
     */
    public static TridentPlayer spawn(NetClient client, String name, UUID uuid) {
        return spawn(client, name, uuid, null);
    }

    /**
     * Spawns a new player.
     *
     * @param client the client representing the player
     * @param name the player name
     * @param uuid the player UUID
     * @param textures the player textures
     */
    public static TridentPlayer spawn(NetClient client, String name, UUID uuid, String textures) {
        TridentWorld world = (TridentWorld) WorldLoader.instance().getDefault();
        TridentPlayer player = new TridentPlayer(client, world, name, uuid);
        player.textures = textures;
        PLAYERS.put(uuid, player);
        TridentTabListManager.getInstance().getGlobalTabList().addPlayer(player);

        Position playerPosition = player.position();
        playerPosition.setY(4);

        client.setPlayer(player);

        client.sendPacket(new PlayOutJoinGame(player, world));
        client.sendPacket(PlayOutPluginMsg.BRAND);
        client.sendPacket(new PlayOutDifficulty(world));
        client.sendPacket(new PlayOutSpawnPos());
        client.sendPacket(new PlayOutPosLook(player));

        int chunkLoadRadius = 3;

        for (int x = playerPosition.getChunkX() - chunkLoadRadius; x <= playerPosition.getChunkX() + chunkLoadRadius; x++) {
            for (int z = playerPosition.getChunkZ() - chunkLoadRadius; z <= playerPosition.getChunkZ() + chunkLoadRadius; z++) {
                TridentChunk chunk = world.chunkAt(x, z);
                client.sendPacket(new PlayOutChunk(chunk));
            }
        }

        ChatComponent chat = ChatComponent.create()
                .setColor(ChatColor.YELLOW)
                .setTranslate("multiplayer.player.joined")
                .addWith(client.name());

        PlayOutSpawnPlayer newPlayerPacket = new PlayOutSpawnPlayer(player);

        TridentServer.instance().players().forEach(p -> {
            p.sendMessage(chat, ChatType.CHAT);

            if(!p.equals(player)) {
                ((TridentPlayer) p).net().sendPacket(newPlayerPacket);

                PlayOutSpawnPlayer oldPlayerPacket = new PlayOutSpawnPlayer(p);
                player.net().sendPacket(oldPlayerPacket);
            }
        });

        return player;
    }

    /**
     * Resumes the joining process after the player has
     * confirmed the client spawn position.
     */
    public void resumeLogin() {
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
    public void doRemove() {
        PLAYERS.remove(this.uuid);
        TridentTabListManager.getInstance().getGlobalTabList().removePlayer(this);

        ChatComponent chat = ChatComponent.create()
                .setColor(ChatColor.YELLOW)
                .setTranslate("multiplayer.player.left")
                .addWith(this.name);
        PLAYERS.values().forEach(e -> e.sendMessage(chat, ChatType.CHAT));
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public UUID uuid() {
        return this.uuid;
    }

    @Override
    public void sendMessage(ChatComponent chat, ChatType type) {
        this.net().sendPacket(new PlayOutChat(chat, type));
    }

    @Override
    public void kick(ChatComponent reason) {
        this.client.disconnect(reason);
    }

    @Override
    public TabList getTabList() {
        return tabList;
    }

    @Override
    public void setTabList(TabList tabList) {
        TridentTabListManager.getInstance().setTabList(this, this.tabList, tabList);
        this.tabList = tabList;
        ((TridentTabList) tabList).sendToPlayer(this);
    }

    public String getTextures() {
        return textures;
    }

    public void setTextures(String textures) {
        this.textures = textures;
        // TODO Push update to tablist and other players
    }

}