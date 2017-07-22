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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.Getter;
import net.tridentsdk.doc.Policy;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.ui.chat.ChatComponent;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;

/**
 * Implementation of a global tablist, which contains all
 * players on the server.
 */
@Policy("singleton")
@ThreadSafe
public class TridentGlobalTabList extends TridentTabList {
    /**
     * The instance of the global tab list
     */
    @Getter
    private static final TridentGlobalTabList instance = new TridentGlobalTabList();

    /**
     * Creates an initializes a new global tab list
     */
    public TridentGlobalTabList() {
    }

    @Override
    public void setElement(int slot, ChatComponent value) {
        throw new RuntimeException("Attempted to edit global tablist!");
    }

    @Override
    public ChatComponent getElement(int slot) {
        return null;
    }

    @Override
    public void update() {
        synchronized (this.lock) {
            this.elements.clear();

            List<TridentPlayer> players = new ArrayList<>(TridentPlayer.getPlayers().values());
            players.sort(Comparator.comparing(p -> p.getDisplayName().getText()));
            players.forEach(p -> {
                p.featuredTabLists.add(this);
                this.elements.add(new TabListElement(p));
            });
        }

        super.update();
    }
}
