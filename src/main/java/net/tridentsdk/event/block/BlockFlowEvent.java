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
import net.tridentsdk.event.Cancellable;

/**
 * Called when a liquid flows from a block to another
 */
public class BlockFlowEvent extends BlockEvent implements Cancellable {
    private final Block to;
    private boolean cancelled;

    /**
     * @param from Block where the liquid is flowing from
     * @param to   Block where the liquid is flowing too
     */
    public BlockFlowEvent(Block from, Block to) {
        super(from);
        this.to = to;
    }

    /**
     * Return where the block is flowing too
     *
     * @return Block where the liquid is flowing too
     */
    public Block getTo() {
        return this.to;
    }

    /**
     * Return where the liquid is flowing from
     *
     * @return Block where the liquid is flowing from
     */
    public Block getFrom() {
        return super.getBlock();
    }

    /**
     * Return if the event is cancelled
     *
     * @return true if cancelled
     */
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Set if the event is cancelled
     *
     * @param cancel Boolean cancellation state of event
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
