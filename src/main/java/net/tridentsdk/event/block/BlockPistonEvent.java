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
import net.tridentsdk.api.Material;
import net.tridentsdk.api.Orientation;
import net.tridentsdk.api.event.Cancellable;
import net.tridentsdk.api.event.block.*;

/**
 * Called whenever a piston extends or retracts
 */
public abstract class BlockPistonEvent extends net.tridentsdk.api.event.block.BlockEvent implements Cancellable {
    private final Orientation direction;
    private final boolean retract;
    private final Block influenced;
    private boolean cancelled;

    public BlockPistonEvent(Block block, Orientation direction, boolean retract, Block influenced) {
        super(block);
        this.direction = direction;
        this.retract = retract;
        this.influenced = influenced;
    }

    /**
     * Returns the direction that the piston is facing, for example if the piston head face of a block is on the north
     * end of a block, then the Direction that this event returns will be north, does not change depending on whether
     * this piston is extending or retracting, so a block may actually be moving south if this is returns north
     *
     * @return Orientation
     */
    public Orientation getDirection() {
        return this.direction;
    }

    /**
     * Returns true if this piston is retracting
     *
     * @return Boolean
     */
    public boolean isRetracting() {
        return this.retract;
    }

    /**
     * Returns true if this piston is extending, convenience for !isRetracting()
     *
     * @return Boolean
     */
    public boolean isExtending() {
        return !this.retract;
    }

    /**
     * Gets the block that is being moved by this piston, if any
     * <p/>
     * <p>If this is a piston extend event, this will return the first block in the series of blocks being pushed.</p>
     *
     * @return the block being moved, may be null if air, or retracting from a block without this piston being sticky
     */
    public Block getInfluencedBlock() {
        return this.influenced;
    }

    public boolean isSticky() {
        return this.getBlock().getType() == Material.PISTON_STICKY_BASE;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
