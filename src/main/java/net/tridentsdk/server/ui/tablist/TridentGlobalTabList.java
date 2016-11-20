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
package net.tridentsdk.server.ui.tablist;

import net.tridentsdk.chat.ChatComponent;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.server.packet.play.PlayOutTabListItem;
import net.tridentsdk.server.player.TridentPlayer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of a global tablist, which contains all
 * players on the server.
 */
public class TridentGlobalTabList extends TridentTabList {
    /**
     * The instance of the global tab list
     */
    public static TridentGlobalTabList GLOBAL = new TridentGlobalTabList();

    /**
     * The mapping of players to their own tab list element
     */
    private final Map<Player, TabListElement> players;

    /**
     * Creates an initializes a new global tab list
     */
    public TridentGlobalTabList() {
        this.players = new ConcurrentHashMap<>();

        this.setHeader(ChatComponent.text("HEADAAH"));
        this.setFooter(ChatComponent.text("FOOTAAH"));
    }

    @Override
    public void setElement(int slot, ChatComponent value) {
        throw new RuntimeException("Attempted to edit global tablist!");
    }

    @Override
    public ChatComponent getElement(int slot) {
        return null;
    }

    /**
     * Adds a player to the global tab list.
     *
     * @param player the player to add
     */
    public void addPlayer(TridentPlayer player) {
        TabListElement element = new TabListElement(player);
        // TODO Add Ping

        this.elements.add(element);
        this.players.put(player, element);

        PlayOutTabListItem.PlayOutTabListItemAddPlayer packet = PlayOutTabListItem.addPlayerPacket();
        packet.addPlayer(element.getUuid(), element.getName(), element.getGameMode(), element.getPing(), element.getDisplayName());
        this.getUserList().forEach(p -> ((TridentPlayer) p).net().sendPacket(packet));
    }

    /**
     * Removes a player from the global tab list.
     *
     * @param player the player to remove
     */
    public void removePlayer(TridentPlayer player) {
        if (this.players.remove(player) != null) {
            this.elements.remove(this.players.get(player));

            PlayOutTabListItem.PlayOutTabListItemRemovePlayer packet = PlayOutTabListItem.removePlayerPacket();
            packet.removePlayer(player.uuid());
            this.getUserList().forEach(p -> ((TridentPlayer) p).net().sendPacket(packet));
        }
    }
}