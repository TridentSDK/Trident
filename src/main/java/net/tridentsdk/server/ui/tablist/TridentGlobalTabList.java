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
     * The mapping of players to their own tab list element
     */
    private final Map<Player, TabListElement> players;

    /**
     * Creates an initializes a new global tab list
     */
    public TridentGlobalTabList() {
        this.players = new ConcurrentHashMap<>();
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