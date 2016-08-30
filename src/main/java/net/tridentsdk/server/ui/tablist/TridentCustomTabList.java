package net.tridentsdk.server.ui.tablist;

import com.google.common.base.Strings;
import net.tridentsdk.chat.ChatComponent;
import net.tridentsdk.server.packet.play.PlayOutTabListItem;
import net.tridentsdk.server.player.TridentPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of a custom tab list that may be added to
 * a player to be customized.
 */
public class TridentCustomTabList extends TridentTabList {
    /**
     * The maximum length of a player name
     */
    private static final int MAX_NAME_LENGTH = 16;
    /**
     * The alphabet
     */
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * The list of tab list elements
     */
    private final List<TabListElement> elements;

    /**
     * Create and initialize a new custom tab list.
     */
    public TridentCustomTabList() {
        this.elements = new CopyOnWriteArrayList<>();
    }

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
                        TabListElement blank = new TabListElement(UUID.randomUUID());
                        blank.setName(this.getName(i));
                        blank.setBlank(true);
                        blank.setDisplayName(ChatComponent.empty());
                        this.elements.add(i, blank);
                        addedElements.add(blank);
                    }
                }

                TabListElement element = new TabListElement(UUID.randomUUID());
                element.setDisplayName(value);
                element.setName(this.getName(slot));

                this.elements.add(slot, element);
                addedElements.add(element);

                if(addedElements.size() > 0) {
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

                    for (int i = this.elements.size() - 1; i >= 0; i--) {
                        if (this.elements.get(i).isBlank()) {
                            removedElements.add(this.elements.get(i));
                            this.elements.remove(i);
                        } else {
                            break;
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
        return Strings.repeat(String.valueOf(ALPHABET.charAt(position)), count);
    }
}