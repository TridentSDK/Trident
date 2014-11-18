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
import net.tridentsdk.api.event.Event;

public abstract class BlockEvent extends Event {
    private final Block block;

    /**
     * @param block the block associated with the event
     */
    public BlockEvent(Block block) {
        this(block, false);
    }

    /**
     * @param block the block associated with the event
     * @param async if this event is asynchronous
     */
    public BlockEvent(Block block, boolean async) {
        super(async);
        this.block = block;
    }

    /**
     * @return return the block associated with the event
     */
    public Block getBlock() {
        return this.block;
    }
}
