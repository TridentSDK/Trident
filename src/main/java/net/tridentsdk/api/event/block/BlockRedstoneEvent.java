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
package net.tridentsdk.api.event.block;

import net.tridentsdk.api.Block;
import net.tridentsdk.api.event.Cancellable;

/**
 * Called when a block's redstone state is updated, called on each individual section of wire when they change, etc.
 */
public class BlockRedstoneEvent extends BlockEvent implements Cancellable {

    private final int strength;
    private final Block causer;
    private final Cause cause;
    private boolean cancelled;

    /**
     * @param block    Block which redstone state was updated
     * @param strength Integer representing the strength (power level) of the redstone
     * @param causer   Block which caused the redstone update
     * @param cause    Cause for the redstone update
     */
    public BlockRedstoneEvent(Block block, int strength, Block causer, Cause cause) {
        super(block);
        this.strength = strength;
        this.causer = causer;
        this.cause = cause;
    }

    /**
     * Returns the block which caused the redstone update
     *
     * @return Block which caused the redstone updaye
     */
    public Block getCauser() {
        return this.causer;
    }

    /**
     * Returns the cause of the redstone update
     *
     * @return Cause of the redstone update
     */
    public Cause getCause() {
        return this.cause;
    }

    /**
     * Returns the strength (power level) of the redstone
     *
     * @return Integer representing the power level of the redstone
     */
    public int getStrength() {
        return this.strength;
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
     * Representing the cause of a redstone update
     */
    public enum Cause {
        LEVER,
        BUTTON,
        WIRE,
        TORCH,
        PRESSURE_PLATE,
        HOOK,
        TRAP_CHEST,
        SENSOR,
        REPEATER,
        COMPARATOR
    }
}
