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

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.tridentsdk.chat.ChatComponent;
import net.tridentsdk.doc.Policy;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.world.opt.GameMode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A tab list element.
 */
@Data
public class TabListElement {
    /**
     * The player that owns this tab list element
     */
    @Policy("Must release")
    private final TridentPlayer player;
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
    private volatile int ping = 0;
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
    private volatile List<PlayerProperty> properties;

    /**
     * Creates a new tab list element which can be filled
     * with custom fields.
     */
    public TabListElement() {
        this.player = null;
        this.uuid = null;
    }

    /**
     * Creates a new tab list element which is filled with
     * data from the player's properties.
     *
     * @param player the player to fill the element
     */
    public TabListElement(TridentPlayer player) {
        this.player = player;
        this.uuid = player.uuid();
        this.name = player.name();
        this.ping = (int) player.net().getPing();
        this.gameMode = player.getGameMode();

        String textures = player.getTextures();
        if (textures != null) {
            this.properties = new ArrayList<>();
            this.properties.add(new TabListElement.PlayerProperty("textures", textures));
        }
    }

    @Override
    public int hashCode() {
        if (this.player != null) {
            return this.player.hashCode();
        }

        return System.identityHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this.player != null) {
            return this.player.equals(obj);
        }

        return this == obj;
    }

    @Data
    @RequiredArgsConstructor
    public static class PlayerProperty {
        private final String name;
        @NonNull
        private String value;
        private String signature;
    }
}