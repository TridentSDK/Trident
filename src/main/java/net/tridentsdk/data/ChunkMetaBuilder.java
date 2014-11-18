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
import net.tridentsdk.api.world.ChunkLocation;

/**
 * Builds chunk metadata required for chunk packet data sent to the player
 *
 * @author The TridentSDK Team
 */
public class ChunkMetaBuilder implements Writable {
    private ChunkLocation location;
    private short bitmap;

    /**
     * Gets the chunk location
     *
     * @return the location of the chunk built into the metadata
     */
    public ChunkLocation getLocation() {
        return this.location;
    }

    /**
     * Sets the chunk meta location
     *
     * @param location the location to set the chunk to
     * @return the current instance
     */
    public ChunkMetaBuilder setLocation(ChunkLocation location) {
        this.location = location;

        return this;
    }

    /**
     * Reads the {@code short} value that maps the bits in the chunk
     *
     * @return the chunk bitmap
     */
    public short getBitmap() {
        return this.bitmap;
    }

    /**
     * Sets the chunk bitmap
     *
     * @param bitmap the {@code short} that maps the chunk data
     * @return the current instance
     */
    public ChunkMetaBuilder setBitmap(short bitmap) {
        this.bitmap = bitmap;

        return this;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(this.location.getX());
        buf.writeInt(this.location.getZ());

        buf.writeShort((int) this.bitmap);
    }
}
