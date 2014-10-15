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
package net.tridentsdk.data;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.Location;

/**
 * Represents a writable form of a {@link net.tridentsdk.api.Location}
 *
 * @author The TridentSDK Team
 */
public class Position implements Writable {
    private Location loc;

    /**
     * Creates a new position based from an existing location
     *
     * @param loc the location to wrap with writable format
     */
    public Position(Location loc) {
        this.loc = loc;
    }

    /**
     * Gets the wrapped, original location
     *
     * @return the location passed in by constructor or by {@link #setLoc(net.tridentsdk.api.Location)}
     */
    public Location getLoc() {
        return this.loc;
    }

    /**
     * Sets the wrapped position
     *
     * <p>This does not change the value of already written locations. This is purely for purposes of performance, but
     * removes concurrency.</p>
     *
     * @param loc the location to wrap with writable format
     */
    public void setLoc(Location loc) {
        this.loc = loc;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeLong((long) ((int) this.loc.getX() & 0x3FFFFFF) << 38 |
                (long) ((int) this.loc.getY() & 0xFFF) << 26 |
                (long) ((int) this.loc.getZ() & 0x3FFFFFF));
    }
}
