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

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.tridentsdk.doc.Policy;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.ui.chat.ChatComponent;
import net.tridentsdk.world.opt.GameMode;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * A tab list element.
 */
@Data
@ThreadSafe
public class TabListElement {
    /**
     * The UUID of the player at this tab list element
     */
    private final UUID uuid;
    /**
     * The game mode
     */
    private volatile GameMode gameMode;
    /**
     * The name of the player
     */
    private volatile String name;
    /**
     * The player's ping
     */
    private volatile int ping;
    /**
     * The value that is displayed on the scoreboard
     */
    private volatile ChatComponent displayName;
    /**
     * Whether or not this element is blank/empty
     */
    private volatile boolean blank;
    /**
     * The tab list properties (such as skin textures)
     */
    @Policy("Single update")
    private final List<TabListElement.PlayerProperty> properties;

    /**
     * Creates a new tab list element which can be filled
     * with custom fields.
     */
    public TabListElement() {
        this.uuid = UUID.randomUUID();
        this.properties = Collections.emptyList();
    }

    /**
     * Creates a new tab list element which is filled with
     * data from the player's properties.
     *
     * @param player the player to fill the element
     */
    public TabListElement(TridentPlayer player) {
        this.uuid = player.getUuid();
        this.name = player.getName();
        this.displayName = player.getDisplayName();
        this.ping = player.net().getPing().intValue();
        this.gameMode = player.getGameMode();

        TabListElement.PlayerProperty textures = player.getSkinTextures();
        if (textures != null) {
            this.properties = Collections.singletonList(textures);
        } else {
            this.properties = Collections.emptyList();
        }
    }

    @Data
    @RequiredArgsConstructor
    public static class PlayerProperty {
        private final String name;
        @NonNull
        private final String value;
        private final String signature;
    }
}
