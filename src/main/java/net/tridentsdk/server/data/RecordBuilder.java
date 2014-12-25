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

package net.tridentsdk.server.data;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.Codec;

/**
 * Records the state of a block as it was when the snapshot was taken <p/> <p>Note that this may not always represent
 * the most accurate view of the block in question.</p>
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