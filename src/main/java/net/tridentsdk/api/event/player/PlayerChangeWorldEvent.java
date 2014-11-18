/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
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
package net.tridentsdk.api.event.player;

import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.world.World;

/**
 * Called when a player changes worlds
 */
public class PlayerChangeWorldEvent extends PlayerEvent {
    private final World to;
    private final World from;

    public PlayerChangeWorldEvent(Player player, World from, World to) {
        super(player);
        this.to = to;
        this.from = from;
    }

    public World getTo() {
        return this.to;
    }

    public World getFrom() {
        return this.from;
    }
}
