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

import net.tridentsdk.chat.ChatComponent;
import net.tridentsdk.server.packet.play.PlayOutTabListItem;
import net.tridentsdk.server.player.TridentPlayer;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a custom tab list that may be added to
 * a player to be customized.
 */
@ThreadSafe
public class TridentCustomTabList extends TridentTabList {
    /**
     * The maximum length of a player name
     */
    private static final int MAX_NAME_LENGTH = 16;
    /**
     * The alphabet
     */
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Override
    public void setElement(int slot, ChatComponent value) {
        if(value != null) {
            if (this.elements.size() > slot && this.elements.get(slot) != null) {
                this.elements.get(slot).setDisplayName(value);

                PlayOutTabListItem.PlayOutTabListItemUpdateDisplayName packet = PlayOutTabListItem.updatePlayerPacket();
                packet.update(this.elements.get(slot).getUuid(), value);
                this.getUserList().forEach(player -> ((TridentPlayer) player).net().sendPacket(packet));
            } else {
                List<TabListElement> addedElements = new ArrayList<>();

                for (int i = 0; i < slot; i++) {
                    if (this.elements.size() == i || this.elements.get(i) == null) {
                        TabListElement blank = new TabListElement();
                        blank.setName(this.getName(i));
                        blank.setBlank(true);
                        blank.setDisplayName(ChatComponent.empty());
                        this.elements.add(i, blank);
                        addedElements.add(blank);
                    }
                }

                TabListElement element = new TabListElement();
                element.setDisplayName(value);
                element.setName(this.getName(slot));

                this.elements.add(slot, element);
                addedElements.add(element);

                if(!addedElements.isEmpty()) {
                    PlayOutTabListItem.PlayOutTabListItemAddPlayer packet = PlayOutTabListItem.addPlayerPacket();
                    addedElements.forEach(e -> packet.addPlayer(e.getUuid(), e.getName(), e.getGameMode(), e.getPing(), e.getDisplayName()));
                    this.getUserList().forEach(player -> ((TridentPlayer) player).net().sendPacket(packet));
                }
            }
        } else {
            if (this.elements.size() > slot && this.elements.get(slot) != null) {
                if (slot == this.elements.size() - 1) {
                    List<TabListElement> removedElements = new ArrayList<>();

                    removedElements.add(this.elements.get(slot));
                    this.elements.remove(slot);

                    synchronized (this.elements) {
                        for (int i = this.elements.size() - 1; i >= 0; i--) {
                            if (this.elements.get(i).isBlank()) {
                                removedElements.add(this.elements.get(i));
                                this.elements.remove(i);
                            } else {
                                break;
                            }
                        }
                    }

                    PlayOutTabListItem.PlayOutTabListItemRemovePlayer packet = PlayOutTabListItem.removePlayerPacket();
                    removedElements.forEach(e -> packet.removePlayer(e.getUuid()));
                    this.getUserList().forEach(player -> ((TridentPlayer) player).net().sendPacket(packet));
                } else {
                    this.elements.get(slot).setDisplayName(ChatComponent.empty());
                    this.elements.get(slot).setBlank(true);

                    PlayOutTabListItem.PlayOutTabListItemUpdateDisplayName packet = PlayOutTabListItem.updatePlayerPacket();
                    packet.update(this.elements.get(slot).getUuid(), ChatComponent.empty());
                    this.getUserList().forEach(player -> ((TridentPlayer) player).net().sendPacket(packet));
                }
            }
        }
    }

    @Override
    public ChatComponent getElement(int slot) {
        return this.elements.get(slot).getDisplayName();
    }

    /**
     * Gets a name for a slot.
     *
     * @param slot the slot
     * @return a filler name for the slot
     */
    private String getName(int slot) {
        int count = (slot % MAX_NAME_LENGTH) + 1;
        int position = slot / MAX_NAME_LENGTH;
        return new String(new char[count]).replace("\0", String.valueOf(ALPHABET.charAt(position)));
    }
}