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
import net.tridentsdk.api.event.Event;

public class PlayerEvent extends Event {

    private final Player player;

    /**
     * @param player the player associated with the event
     */

    public PlayerEvent(Player player) {
        this(player, false);
    }

    /**
     * @param player the player associated with that event
     * @param async  the boolean that determines if event is asynchronous
     */

    public PlayerEvent(Player player, boolean async) {
        super(async);
        this.player = player;
    }

    /**
     * @return return the player associated with the event
     */

    public final Player getPlayer() {
        return this.player;
    }
}
