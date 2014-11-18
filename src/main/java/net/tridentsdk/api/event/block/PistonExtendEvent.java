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
package net.tridentsdk.api.event.block;

import com.google.common.collect.ImmutableList;
import net.tridentsdk.api.Block;
import net.tridentsdk.api.Orientation;

import java.util.List;

public class PistonExtendEvent extends BlockPistonEvent {
    private final ImmutableList<Block> blocksInfluenced;
    private boolean cancel;

    public PistonExtendEvent(Block block, Orientation direction, List<Block> influenced) {
        super(block, direction, false, influenced.get(0));

        this.blocksInfluenced = ImmutableList.copyOf(influenced);
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
     * Returns an ImmutableList of the blocks that are being pushed by this piston, may be empty
     */
    public List<Block> getBlocksInfluenced() {
        return this.blocksInfluenced;
    }
}
