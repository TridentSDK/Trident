package net.tridentsdk.server.ui.tablist;

import net.tridentsdk.chat.ChatComponent;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.ui.tablist.TabList;
import net.tridentsdk.ui.tablist.TabListManager;

/**
 * The implementation for a tab list manager, which manages
 * the tab lists each player is displayed.
 */
public class TridentTabListManager implements TabListManager {
    /**
     * The singleton instance of the tab list manager
     */
    private static final TridentTabListManager instance = new TridentTabListManager();

    /**
     * The stored instance of the global tablist
     */
    private final TridentGlobalTabList globalTabList;

    /**
     * Creates a new tab list manager, and initailizes the
     * global tab list with the header and footer.
     */
    public TridentTabListManager() {
        this.globalTabList = new TridentGlobalTabList();

        this.globalTabList.setHeader(ChatComponent.text("HEADAAH"));
        this.globalTabList.setFooter(ChatComponent.text("FOOTAAH"));
    }

    /**
     * Obtains the instance of the tab list manager.
     *
     * @return the instance
     */
    public static TridentTabListManager getInstance() {
        return instance;
    }

    @Override
    public TridentGlobalTabList getGlobalTabList() {
        return this.globalTabList;
    }

    @Override
    public TabList newTabList() {
        return new TridentCustomTabList();
    }

    /**
     * Sets the tab list of the player by removing them
     * from
     * the old tab list and adding them to the new tab
     * list.
     *
     * @param player the player to change tab lists
     * @param oldTabList the old tab list to remove
     * @param newTabList the new tab list to add
     */
    public void setTabList(Player player, TabList oldTabList, TabList newTabList){
        if(oldTabList != null) {
            oldTabList.removeUser(player);
        }

        if(newTabList != null) {
            newTabList.addUser(player);
        }
    }
}