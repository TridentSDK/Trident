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
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.play.PlayOutPlayerListHeaderAndFooter;
import net.tridentsdk.server.packet.play.PlayOutTabListItem;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.ui.chat.ChatComponent;
import net.tridentsdk.ui.tablist.TabList;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The tab list implementation.
 */
@ThreadSafe
public abstract class TridentTabList implements TabList {
    /**
     * The players which are displayed this tab list
     */
    protected final Collection<TridentPlayer> users = Collections.newSetFromMap(new ConcurrentHashMap<>());
    /**
     * Elements of this tab list
     */
    @GuardedBy("lock")
    protected final List<TabListElement> elements = new ArrayList<>();
    /**
     * Lock for the elements/lastSeen collections
     */
    protected final Object lock = new Object();

    /**
     * The tab list header
     */
    @Getter
    private volatile ChatComponent header;
    /**
     * Tab list footer
     */
    @Getter
    private volatile ChatComponent footer;

    @Override
    public void setHeader(ChatComponent value) {
        this.header = value;
        this.updateHeaderFooter();
    }

    @Override
    public void setFooter(ChatComponent value) {
        this.footer = value;
        this.updateHeaderFooter();
    }

    @Override
    public Collection<Player> getUserList() {
        return Collections.unmodifiableCollection(this.users);
    }

    public void subscribe(Player player) {
        TridentPlayer tridentPlayer = (TridentPlayer) player;
        this.users.add(tridentPlayer);

        PlayOutTabListItem.AddPlayer addPacket = PlayOutTabListItem.addPlayerPacket();
        PlayOutPlayerListHeaderAndFooter headerAndFooterPacket = new PlayOutPlayerListHeaderAndFooter(this.header, this.footer);
        TabListElement element = new TabListElement(tridentPlayer);

        synchronized (this.lock) {
            this.elements.add(element);
            this.elements.forEach(addPacket::addPlayer);
        }

        NetClient net = tridentPlayer.net();
        net.sendPacket(addPacket);
        net.sendPacket(headerAndFooterPacket);

        PlayOutTabListItem.AddPlayer addMe = PlayOutTabListItem.addPlayerPacket();
        addMe.addPlayer(element);
        for (TridentPlayer tp : this.users) {
            tp.net().sendPacket(addMe);
        }
    }

    public void unsubscribe(Player player) {
        TridentPlayer tridentPlayer = (TridentPlayer) player;
        this.users.remove(tridentPlayer);

        PlayOutTabListItem.RemovePlayer packet = PlayOutTabListItem.removePlayerPacket();

        synchronized (this.lock) {
            for (Iterator<TabListElement> it = this.elements.iterator(); it.hasNext(); ) {
                TabListElement element = it.next();
                if (element.getUuid().equals(tridentPlayer.getUuid())) {
                    it.remove();
                }

                packet.removePlayer(element.getUuid());
            }
        }

        NetClient net = tridentPlayer.net();
        net.sendPacket(packet);
        net.sendPacket(new PlayOutPlayerListHeaderAndFooter(ChatComponent.empty(), ChatComponent.empty()));

        PlayOutTabListItem.RemovePlayer removeMe = PlayOutTabListItem.removePlayerPacket();
        removeMe.removePlayer(tridentPlayer.getUuid());
        for (TridentPlayer tp : this.users) {
            tp.net().sendPacket(removeMe);
        }
    }

    /**
     * Updates the player's tab list name.
     *
     * @param player the player whose tab list name is to be
     * updated
     */
    public void updateTabListName(Player player) {
        PlayOutTabListItem.UpdateDisplayName updateDisplayName = PlayOutTabListItem.updatePlayerPacket();
        updateDisplayName.update(player.getUuid(), player.getTabListName());

        for (TridentPlayer tp : this.users) {
            tp.net().sendPacket(updateDisplayName);
        }
    }

    /**
     * Refreshes the player's entry in the tab list.
     *
     * @param player the player to update
     */
    public void update(TridentPlayer player) {
        PlayOutTabListItem.RemovePlayer removeMe = PlayOutTabListItem.removePlayerPacket();
        removeMe.removePlayer(player.getUuid());

        PlayOutTabListItem.AddPlayer addMe = PlayOutTabListItem.addPlayerPacket();
        addMe.addPlayer(new TabListElement(player));

        for (TridentPlayer tp : this.users) {
            tp.net().sendPacket(removeMe);
            tp.net().sendPacket(addMe);
        }
    }

    /**
     * Update operation if the header or footer fields of
     * the tab list are updated.
     */
    private void updateHeaderFooter() {
        PlayOutPlayerListHeaderAndFooter packet = new PlayOutPlayerListHeaderAndFooter(this.header, this.footer);
        this.users.forEach(player -> player.net().sendPacket(packet));
    }
}