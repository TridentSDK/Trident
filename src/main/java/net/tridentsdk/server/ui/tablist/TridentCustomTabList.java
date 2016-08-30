package net.tridentsdk.server.ui.tablist;

import com.google.common.base.Strings;
import net.tridentsdk.chat.ChatComponent;
import net.tridentsdk.server.packet.play.PlayOutTabListItem;
import net.tridentsdk.server.player.TridentPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class TridentCustomTabList extends TridentTabList {

    private static final int maxNameLength = 16;
    private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private CopyOnWriteArrayList<TabListElement> elements;

    public TridentCustomTabList() {
        this.elements = new CopyOnWriteArrayList<>();
    }

    @Override
    public void setElement(int slot, ChatComponent value) {
        if(value != null) {
            if(elements.size() > slot && elements.get(slot) != null) {
                elements.get(slot).setDisplayName(value);

                PlayOutTabListItem.PlayOutTabListItemUpdateDisplayName packet = PlayOutTabListItem.updatePlayerPacket();
                packet.update(elements.get(slot).getUuid(), value);
                getUserList().forEach(player -> ((TridentPlayer) player).net().sendPacket(packet));
            } else {
                List<TabListElement> addedElements = new ArrayList<>();

                for (int i = 0; i < slot; i++) {
                    if(elements.size() == i || elements.get(i) == null) {
                        TabListElement blank = new TabListElement(UUID.randomUUID());
                        blank.setName(getName(i));
                        blank.setBlank(true);
                        blank.setDisplayName(ChatComponent.empty());
                        elements.add(i, blank);
                        addedElements.add(blank);
                    }
                }

                TabListElement element = new TabListElement(UUID.randomUUID());
                element.setDisplayName(value);
                element.setName(getName(slot));

                elements.add(slot, element);
                addedElements.add(element);

                if(addedElements.size() > 0) {
                    PlayOutTabListItem.PlayOutTabListItemAddPlayer packet = PlayOutTabListItem.addPlayerPacket();
                    addedElements.forEach(e -> packet.addPlayer(e.getUuid(), e.getName(), e.getGameMode(), e.getPing(), e.getDisplayName()));
                    getUserList().forEach(player -> ((TridentPlayer) player).net().sendPacket(packet));
                }
            }
        } else {
            if(elements.size() > slot && elements.get(slot) != null) {
                System.out.println("REMOVING " + slot + " WITH SIZE " + elements.size());

                if(slot == elements.size() - 1) {
                    List<TabListElement> removedElements = new ArrayList<>();

                    removedElements.add(elements.get(slot));
                    elements.remove(slot);

                    for (int i = elements.size() - 1; i >= 0; i--) {
                        if(elements.get(i).isBlank()) {
                            removedElements.add(elements.get(i));
                            elements.remove(i);
                        } else {
                            break;
                        }
                    }

                    PlayOutTabListItem.PlayOutTabListItemRemovePlayer packet = PlayOutTabListItem.removePlayerPacket();
                    removedElements.forEach(e -> packet.removePlayer(e.getUuid()));
                    getUserList().forEach(player -> ((TridentPlayer) player).net().sendPacket(packet));
                } else {
                    elements.get(slot).setDisplayName(ChatComponent.empty());
                    elements.get(slot).setBlank(true);

                    PlayOutTabListItem.PlayOutTabListItemUpdateDisplayName packet = PlayOutTabListItem.updatePlayerPacket();
                    packet.update(elements.get(slot).getUuid(), ChatComponent.empty());
                    getUserList().forEach(player -> ((TridentPlayer) player).net().sendPacket(packet));
                }
            }
        }
    }

    @Override
    public ChatComponent getElement(int slot) {
        return elements.get(slot).getDisplayName();
    }

    private String getName(int slot) {
        int count = (slot % maxNameLength) + 1;
        int position = slot / maxNameLength;
        return Strings.repeat(String.valueOf(alphabet.charAt(position)), count);
    }

}
