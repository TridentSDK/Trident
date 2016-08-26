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
import net.tridentsdk.chat.Chat;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.server.entity.TridentEntity;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.play.*;
import net.tridentsdk.server.world.TridentChunk;
import net.tridentsdk.server.world.TridentWorld;
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
    // TODO client setting
    // TODO chunks
    // TODO account for login count

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

    /**
     * Constructs a new player.
     */
    private TridentPlayer(NetClient client, World world, String name, UUID uuid) {
        super(world);
        this.client = client;
        this.name = name;
        this.uuid = uuid;
    }

    /**
     * Spawns a new player.
     *
     * @param client the client representing the player
     * @param name the player name
     * @param uuid the player UUID
     */
    public static void spawn(NetClient client, String name, UUID uuid) {
        TridentWorld world = (TridentWorld) WorldLoader.instance().getDefault();
        TridentPlayer player = new TridentPlayer(client, world, name, uuid);
        PLAYERS.put(uuid, player);
        client.setPlayer(player);

        client.sendPacket(new PlayOutJoinGame(player, world));
        client.sendPacket(PlayOutPluginMsg.BRAND);
        client.sendPacket(new PlayOutDifficulty(world));
        client.sendPacket(new PlayOutSpawnPos());
        client.sendPacket(new PlayOutPosLook(player));

        int chunkLoadRadius = 7;

        for (int i = -chunkLoadRadius; i < chunkLoadRadius; i++) {
            for (int j = -chunkLoadRadius; j < chunkLoadRadius; j++) {
                TridentChunk chunk = world.chunkAt(i, j);
                client.sendPacket(new PlayOutChunk(chunk));
            }
        }
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
    public void kick(Chat reason) {
        this.client.disconnect(reason);
    }
}