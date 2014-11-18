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
import net.tridentsdk.api.event.Cancellable;

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
