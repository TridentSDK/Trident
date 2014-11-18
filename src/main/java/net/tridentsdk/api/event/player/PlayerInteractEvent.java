/*
 *     Trident - A Multithreaded Server Alternative
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
package net.tridentsdk.api.event.player;

import net.tridentsdk.api.Block;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.event.Cancellable;

public class PlayerInteractEvent extends PlayerEvent implements Cancellable {

    private boolean cancel;

    private Block block;

    /**
     * TODO add action detection and blockface(?)
     *
     * @param player the player associated with the event
     * @param block  the block that was interacted with (null if none)
     */

    public PlayerInteractEvent(Player player, Block block) {
        super(player);
        this.setBlock(block);
    }

    /**
     * @return return the interacted block (null means no block was interacted with)
     */

    public Block getBlock() {
        return this.block;
    }

    /**
     * @param block the block that was interacted with
     */

    public void setBlock(Block block) {
        this.block = block;
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
