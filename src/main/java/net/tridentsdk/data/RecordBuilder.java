/*
 * Copyright (c) 2014, TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of TridentSDK nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.data;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.Codec;

/**
 * Records the state of a block as it was when the snapshot was taken
 *
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