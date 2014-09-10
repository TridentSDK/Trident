/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *     3. Neither the name of TridentSDK nor the names of its
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

public class RecordBuilder {

    private volatile byte x;
    private volatile byte y;
    private volatile byte z;
    private volatile int blockId;

    public RecordBuilder() {
    }

    public byte getX() {
        return x;
    }

    public RecordBuilder setX(byte x) {
        this.x = x;

        return this;
    }

    public byte getY() {
        return y;
    }

    public RecordBuilder setY(byte y) {
        this.y = y;

        return this;
    }

    public byte getZ() {
        return z;
    }

    public RecordBuilder setZ(byte z) {
        this.z = z;

        return this;
    }

    public int getBlockId() {
        return blockId;
    }

    public RecordBuilder setBlockId(int blockId) {
        this.blockId = blockId;

        return this;
    }

    public RecordBuilder write(ByteBuf buf) {
        Codec.writeVarInt32(buf, blockId);

        return this;
    }
}