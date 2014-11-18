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
package net.tridentsdk.api.event.block;

import net.tridentsdk.api.Block;
import net.tridentsdk.api.Material;
import net.tridentsdk.api.Orientation;
import net.tridentsdk.api.event.Cancellable;

/**
 * Called whenever a piston extends or retracts
 */
public abstract class BlockPistonEvent extends BlockEvent implements Cancellable {
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
