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
