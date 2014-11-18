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

import net.tridentsdk.api.Location;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.event.Cancellable;

public class PlayerMoveEvent extends PlayerEvent implements Cancellable {

    private final Location fromLoc;
    private final Location toLoc;
    private boolean cancel;

    public PlayerMoveEvent(Player player, Location from, Location to) {
        super(player);
        this.fromLoc = from;
        this.toLoc = to;
    }

    /**
     * Return previous location
     *
     * @return returns the previous player location
     */

    public Location getFromLocation() {
        return this.fromLoc;
    }

    /**
     * Return next location
     *
     * @return returns the next player location
     */

    public Location getToLocation() {
        return this.toLoc;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
