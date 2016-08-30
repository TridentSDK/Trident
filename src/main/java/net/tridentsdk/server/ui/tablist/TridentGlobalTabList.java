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

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TridentGlobalTabList extends TridentTabList {

    private final Map<Player, TabListElement> players;

    public TridentGlobalTabList() {
        players = new ConcurrentHashMap<>();
    }

    @Override
    public void setElement(int slot, ChatComponent value) {
        throw new RuntimeException("Attempted to edit global tablist!");
    }

    @Override
    public ChatComponent getElement(int slot) {
        return null;
    }

    public void addPlayer(TridentPlayer player) {
        TabListElement element = new TabListElement(player.uuid());
        element.setName(player.name());

        if(player.getTextures() != null) {
            element.setProperties(new ArrayList<>());
            element.getProperties().add(new TabListElement.PlayerProperty("textures", player.getTextures()));
        }

        element.setGameMode(player.world().opts().gameMode()); // TODO Change to player gamemode
        // TODO Add Ping

        elements.add(element);
        players.put(player, element);

        PlayOutTabListItem.PlayOutTabListItemAddPlayer packet = PlayOutTabListItem.addPlayerPacket();
        packet.addPlayer(element.getUuid(), element.getName(), element.getGameMode(), element.getPing(), element.getDisplayName());
        getUserList().forEach(p -> ((TridentPlayer) p).net().sendPacket(packet));
    }

    public void removePlayer(TridentPlayer player) {
        if(players.containsKey(player)) {
            elements.remove(players.get(player));
            players.remove(player);

            PlayOutTabListItem.PlayOutTabListItemRemovePlayer packet = PlayOutTabListItem.removePlayerPacket();
            packet.removePlayer(player.uuid());
            getUserList().forEach(p -> ((TridentPlayer) p).net().sendPacket(packet));
        }
    }

}
