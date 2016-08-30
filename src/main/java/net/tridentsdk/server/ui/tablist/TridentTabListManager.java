package net.tridentsdk.server.ui.tablist;

import net.tridentsdk.chat.ChatComponent;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.ui.tablist.TabList;
import net.tridentsdk.ui.tablist.TabListManager;

public class TridentTabListManager implements TabListManager {

    private static final TridentTabListManager instance = new TridentTabListManager();

    private final TridentGlobalTabList globalTabList;

    public TridentTabListManager() {
        this.globalTabList = new TridentGlobalTabList();

        this.globalTabList.setHeader(ChatComponent.text("HEADAAH"));
        this.globalTabList.setFooter(ChatComponent.text("FOOTAAH"));
    }

    public static TridentTabListManager getInstance() {
        return instance;
    }

    @Override
    public TridentGlobalTabList getGlobalTabList() {
        return globalTabList;
    }

    @Override
    public TabList newTabList() {
        return new TridentCustomTabList();
    }

    public void setTabList(Player player, TabList oldTabList, TabList newTabList){
        if(oldTabList != null) {
            oldTabList.removeUser(player);
        }

        if(newTabList != null) {
            newTabList.addUser(player);
        }
    }

}
