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
