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

import net.tridentsdk.api.entity.Item;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.event.player.*;

public class PlayerConsumeEvent extends net.tridentsdk.api.event.player.PlayerHungerEvent {

    private final Item item;
    private boolean cancel;

    /**
     * @param player the player associated with this event
     * @param feed   the amount of hunger replenished
     * @param item   the item consumed
     */

    public PlayerConsumeEvent(Player player, Item item, double feed) {
        super(player, feed);
        this.setReplenishAmount(feed);
        this.item = item;
    }

    /**
     * @return return the amount of hunger replenished
     */

    public double getReplenishAmount() {
        return super.getFeed();
    }

    /**
     * @param feed the amount of hunger replenished
     */

    public void setReplenishAmount(double feed) {
        super.setFeed(feed);
    }

    /**
     * @return return the item consumed
     */

    public Item getFood() {
        return this.item;
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
