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

import net.tridentsdk.Block;
import net.tridentsdk.BlockFace;
import net.tridentsdk.Material;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.event.Cancellable;

/**
 * Called whenever a block is placed
 */
public class BlockPlaceEvent extends BlockEvent implements Cancellable {
    private final Player player;
    private final Block blockClicked;
    private final BlockFace faceClicked;

    private boolean cancel;

    /**
     * @param player       Player who placed this block
     * @param block        Block that was placed
     * @param blockClicked Block
     * @param faceClicked  BlockFace
     */
    public BlockPlaceEvent(Player player, Block block, Block blockClicked, BlockFace faceClicked) {
        super(block);
        this.player = player;
        this.blockClicked = blockClicked;
        this.faceClicked = faceClicked;
    }

    /**
     * Gets the block face of the block that was clicked on to place this block
     *
     * @return BlockFace of the clicked block
     */
    public BlockFace getFaceClicked() {
        return this.faceClicked;
    }

    /**
     * Get the Material of the placed block
     *
     * @return Material of the placed block
     */
    public Material getTypePlaced() {
        return this.getBlock().getMaterial();
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
     * Gets the block clicked on to place this block
     *
     * @return Block that was clicked
     */
    public Block getBlockClicked() {
        return this.blockClicked;
    }

    /**
     * Returns the player associated with this event
     *
     * @return Player who placed the block
     */
    public Player getPlayer() {
        return this.player;
    }
}
