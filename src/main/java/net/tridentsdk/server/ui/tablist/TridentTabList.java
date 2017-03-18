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
import net.tridentsdk.chat.ChatComponent;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.server.packet.play.PlayOutPlayerListHeaderAndFooter;
import net.tridentsdk.server.packet.play.PlayOutTabListItem;
import net.tridentsdk.server.player.TridentPlayer;
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
    protected final Collection<Player> users = Collections.newSetFromMap(new ConcurrentHashMap<>());
    /**
     * The last observed list of elements belonging to this
     * tab list's subscribers
     */
    private final Set<TabListElement> lastSeen = Collections.newSetFromMap(new ConcurrentHashMap<>());
    /**
     * Elements of this tab list
     */
    @GuardedBy("lock")
    protected final List<TabListElement> elements = new ArrayList<>();
    /**
     * Lock for the element list
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

    @Override
    public void subscribe(Player player) {
        this.users.add(player);
    }

    @Override
    public void unsubscribe(Player player) {
        this.users.remove(player);
    }

    /**
     * Sends the tab list to all subscribed players.
     */
    public void update() {
        if (this.users.isEmpty())
            return;

        PlayOutPlayerListHeaderAndFooter headerAndFooterPacket = new PlayOutPlayerListHeaderAndFooter(this.header, this.footer);
        PlayOutTabListItem.PlayOutTabListItemAddPlayer addPacket = PlayOutTabListItem.addPlayerPacket();
        PlayOutTabListItem.PlayOutTabListItemRemovePlayer removePacket = PlayOutTabListItem.removePlayerPacket();
        PlayOutTabListItem.PlayOutTabListItemUpdateDisplayName updatePacket = PlayOutTabListItem.updatePlayerPacket();

        Map<UUID, TabListElement> lastSeen = new LinkedHashMap<>();
        Map<UUID, TabListElement> current = new LinkedHashMap<>();

        this.lastSeen.forEach(e -> lastSeen.put(e.getUuid(), e));
        synchronized (this.lock) {
            this.elements.forEach(e -> current.put(e.getUuid(), e));
        }

        if (current.containsKey(null)) {
            throw new IllegalStateException("tablist currently has a null uuid (= " + current.get(null) + ")");
        }

        lastSeen.entrySet()
                .stream()
                .filter(e -> !current.containsKey(e.getKey()))
                .forEach(e -> removePacket.removePlayer(e.getKey()));

        current.entrySet()
                .stream()
                .filter(e -> !lastSeen.containsKey(e.getKey()))
                .forEach(e -> addPacket.addPlayer(e.getValue()));

        current.entrySet()
                .stream()
                .filter(e -> lastSeen.containsKey(e.getKey()))
                .filter(e -> !Objects.equals(e.getValue().getDisplayName(), lastSeen.get(e.getKey()).getDisplayName()))
                .forEach(e -> updatePacket.update(e.getKey(), e.getValue().getDisplayName()));

        synchronized (this.lock) {
            this.lastSeen.clear();
            this.lastSeen.addAll(this.elements);
        }

        this.users.forEach(p -> {
            TridentPlayer player = (TridentPlayer) p;
            if (removePacket.getActionCount() > 0)
                player.net().sendPacket(removePacket);
            if (addPacket.getActionCount() > 0)
                player.net().sendPacket(addPacket);
            if (updatePacket.getActionCount() > 0)
                player.net().sendPacket(updatePacket);
            player.net().sendPacket(headerAndFooterPacket);
        });
    }

    public void forceSend(TridentPlayer player) {
        PlayOutTabListItem.PlayOutTabListItemAddPlayer addPacket = PlayOutTabListItem.addPlayerPacket();
        PlayOutPlayerListHeaderAndFooter headerAndFooterPacket = new PlayOutPlayerListHeaderAndFooter(this.header, this.footer);

        synchronized (this.lock) {
            this.elements.forEach(addPacket::addPlayer);
        }

        player.net().sendPacket(addPacket);
        player.net().sendPacket(headerAndFooterPacket);
    }

    /**
     * Update operation if the header or footer fields of
     * the tab list are updated.
     */
    private void updateHeaderFooter() {
        PlayOutPlayerListHeaderAndFooter packet = new PlayOutPlayerListHeaderAndFooter(this.header, this.footer);
        this.users.forEach(player -> ((TridentPlayer) player).net().sendPacket(packet));
    }
}
