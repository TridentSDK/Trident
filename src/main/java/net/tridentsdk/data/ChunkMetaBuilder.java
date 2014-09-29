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
