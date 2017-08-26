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
package net.tridentsdk.server.ui.tablist;

import lombok.Getter;
import net.tridentsdk.doc.Policy;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.server.packet.play.PlayOutTabListItem;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.ui.chat.ChatComponent;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Comparator;

/**
 * Implementation of a global tablist, which contains all
 * players on the server.
 */
@Policy("singleton")
@ThreadSafe
public class TridentGlobalTabList extends TridentTabList {
    /**
     * The instance of the global tab list
     */
    @Getter
    private static final TridentGlobalTabList instance = new TridentGlobalTabList();

    /**
     * Creates an initializes a new global tab list
     */
    private TridentGlobalTabList() {
    }

    @Override
    public void setElement(int slot, ChatComponent value) {
        throw new RuntimeException("Attempted to edit global tablist!");
    }

    @Override
    public ChatComponent getElement(int slot) {
        throw new RuntimeException("Attempted to grab element on global tablist");
    }

    @Override
    public void subscribe(Player player) {
        this.users.add((TridentPlayer) player);

        PlayOutTabListItem.RemovePlayer removeAll = PlayOutTabListItem.removePlayerPacket();
        PlayOutTabListItem.AddPlayer addAll = PlayOutTabListItem.addPlayerPacket();
        synchronized (this.lock) {
            for (TabListElement element : this.elements) {
                removeAll.removePlayer(element.getUuid());
            }

            this.elements.clear();

            TridentPlayer.getPlayers().values().
                    stream().
                    sorted(Comparator.comparing(p -> p.getTabListName().getText())).
                    forEach(p -> {
                        TabListElement e = new TabListElement(p);
                        this.elements.add(e);
                        addAll.addPlayer(e);
                    });
        }

        for (TridentPlayer p : this.users) {
            p.net().sendPacket(removeAll);
            p.net().sendPacket(addAll);
        }
    }

    @Override
    public void unsubscribe(Player player) {
        super.unsubscribe(player);
    }
}