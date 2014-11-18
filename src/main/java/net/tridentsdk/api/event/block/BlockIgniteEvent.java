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
 * Called when something lights a block on fire
 */
public class BlockIgniteEvent extends BlockEvent implements Cancellable {
    private final Cause cause;
    private boolean cancelled;

    /**
     * @param block Block associated with this event
     * @param cause Cause of this event
     */
    public BlockIgniteEvent(Block block, Cause cause) {
        super(block);
        this.cause = cause;
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

    /**
     * Returns the cause of this event
     *
     * @return Cause of this event
     */
    public Cause getCause() {
        return this.cause;
    }

    /**
     * Cause of block ignition
     */
    public enum Cause {
        FIRE_SPREAD,
        PLAYER,
        LIGHTENING,
        LAVA,
        FIREBALL
    }
}
