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
import net.tridentsdk.server.netty.Codec;

/**
 * Records the state of a block as it was when the snapshot was taken
 * <p/>
 * <p>Note that this may not always represent the most accurate view of the block in question.</p>
 *
 * @author The TridentSDK Team
 */
public class RecordBuilder implements Writable {
    private volatile byte x;
    private volatile byte y;
    private volatile byte z;
    private volatile int blockId;

    /**
     * Get the X location of the block
     *
     * @return the block's X location
     */
    public byte getX() {
        return this.x;
    }

    /**
     * Sets the X location of the block
     *
     * @param x the new X value
     * @return the current instance
     */
    public RecordBuilder setX(byte x) {
        this.x = x;

        return this;
    }

    /**
     * Get the Y location of the block
     *
     * @return the block's Y location
     */
    public byte getY() {
        return this.y;
    }

    /**
     * Sets the Y location of the block
     *
     * @param y the new Y value
     * @return the current instance
     */
    public RecordBuilder setY(byte y) {
        this.y = y;

        return this;
    }

    /**
     * Get the Z location of the block
     *
     * @return the block's Z location
     */
    public byte getZ() {
        return this.z;
    }

    /**
     * Sets the Z location of the block
     *
     * @param z the new Z value
     * @return the current instance
     */
    public RecordBuilder setZ(byte z) {
        this.z = z;

        return this;
    }

    /**
     * Gets the block ID number
     *
     * @return the ID number of the block
     */
    public int getBlockId() {
        return this.blockId;
    }

    /**
     * Sets the block ID number
     *
     * @param blockId the ID number of the block to be set
     * @return the current instance
     */
    public RecordBuilder setBlockId(int blockId) {
        this.blockId = blockId;

        return this;
    }

    @Override
    public void write(ByteBuf buf) {
        Codec.writeVarInt32(buf, this.blockId);
    }
}