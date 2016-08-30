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

    private List<Player> users;
    private ChatComponent header;
    private ChatComponent footer;
    protected List<TabListElement> elements;

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
