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
package net.tridentsdk.event.block;

import net.tridentsdk.api.Block;
import net.tridentsdk.api.BlockFace;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.event.Cancellable;
import net.tridentsdk.api.event.block.*;
import net.tridentsdk.api.inventory.ItemStack;

/**
 * Called whenever a Block is broken
 */
public class BlockBreakEvent extends net.tridentsdk.api.event.block.BlockEvent implements Cancellable {

    private final Player player;
    private final BlockFace blockFace;
    private final ItemStack itemInHand;
    private boolean cancel;

    /**
     * @param player     Player associated with this event
     * @param block      Block associated with this event
     * @param blockFace  BlockFace
     * @param itemInHand ItemStack
     */
    public BlockBreakEvent(Player player, Block block, BlockFace blockFace, ItemStack itemInHand) {
        super(block);
        this.player = player;
        this.blockFace = blockFace;
        this.itemInHand = itemInHand;
    }

    /**
     * Return if the event is cancelled
     *
     * @return true if cancelled
     */
    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    /**
     * Set if the event is cancelled
     *
     * @param cancel Boolean cancellation state of event
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * Returns the item in the player's hand
     *
     * @return ItemStack in the player's hand
     */
    public ItemStack getItemInHand() {
        return this.itemInHand;
    }

    /**
     * Returns the block face clicked to break this block
     *
     * @return BlockFlace of the clicked block
     */
    public BlockFace getBlockFace() {
        return this.blockFace;
    }

    /**
     * Get the player associated with this event
     *
     * @return Player assoctaed with this event
     */
    public Player getPlayer() {
        return this.player;
    }
}
