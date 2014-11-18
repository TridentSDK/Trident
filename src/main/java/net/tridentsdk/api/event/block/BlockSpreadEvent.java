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

/**
 * Called when a block spreads, like grass or mycelium
 */
public class BlockSpreadEvent extends BlockGrowthEvent {
    private final Block blockFrom;

    /**
     * @param to   Block representing the location of the spread
     * @param from Block which represents the origin
     */
    public BlockSpreadEvent(Block to, Block from) {
        super(to);
        this.blockFrom = from;
    }

    /**
     * Returns the origin block of the spread
     *
     * @return Block representing the origin of the spread
     */
    public Block getFrom() {
        return this.blockFrom;
    }
}
