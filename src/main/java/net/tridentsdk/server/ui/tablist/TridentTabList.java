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
import net.tridentsdk.server.packet.play.PlayOutPlayerListHeaderAndFooter;
import net.tridentsdk.server.packet.play.PlayOutTabListItem;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.ui.tablist.TabList;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class TridentTabList implements TabList {

    private final List<Player> users;
    private ChatComponent header;
    private ChatComponent footer;
    protected final List<TabListElement> elements;

    public TridentTabList() {
        this.users = new CopyOnWriteArrayList<>();
        this.elements = new CopyOnWriteArrayList<>();
    }

    @Override
    public void setHeader(ChatComponent value) {
        this.header = value;
        updateHeaderFooter();
    }

    @Override
    public ChatComponent getHeader() {
        return header;
    }

    @Override
    public void setFooter(ChatComponent value) {
        this.footer = value;
        updateHeaderFooter();
    }

    @Override
    public ChatComponent getFooter() {
        return footer;
    }

    @Override
    public List<Player> getUserList() {
        return users;
    }

    @Override
    public void addUser(Player player) {
        if(!users.contains(player)) {
            users.add(player);
        }
    }

    @Override
    public void removeUser(Player player) {
        users.remove(player);
    }

    public void sendToPlayer(TridentPlayer player) {
        PlayOutTabListItem.PlayOutTabListItemAddPlayer itemPacket = PlayOutTabListItem.addPlayerPacket();
        elements.forEach(element -> itemPacket.addPlayer(element.getUuid(), element.getName(), element.getGameMode(), element.getPing(), element.getDisplayName()));
        player.net().sendPacket(itemPacket);

        PlayOutPlayerListHeaderAndFooter headerAndFooterPacket = new PlayOutPlayerListHeaderAndFooter(header, footer);
        player.net().sendPacket(headerAndFooterPacket);
    }

    private void updateHeaderFooter() {
        PlayOutPlayerListHeaderAndFooter packet = new PlayOutPlayerListHeaderAndFooter(header, footer);
        getUserList().forEach(player -> ((TridentPlayer) player).net().sendPacket(packet));
    }

}
