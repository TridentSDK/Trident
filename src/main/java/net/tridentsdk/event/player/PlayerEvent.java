/*
 *     TridentSDK - A Minecraft Server API
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.event.player;

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
