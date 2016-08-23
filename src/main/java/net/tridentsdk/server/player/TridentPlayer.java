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

import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.play.PlayOutJoinGame;

import java.util.UUID;

/**
 * This class is the implementation of a Minecraft client
 * that is represented by a physical entity in a world.
 */
public class TridentPlayer {
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

    public TridentPlayer(NetClient client, String name, UUID uuid) {
        this.client = client;
        this.name = name;
        this.uuid = uuid;
    }

    public static void spawn(NetClient client, String name, UUID uuid) {
        TridentPlayer player = new TridentPlayer(client, name, uuid);

        client.sendPacket(new PlayOutJoinGame(null));
    }
}