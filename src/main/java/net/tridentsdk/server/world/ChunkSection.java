/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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
package net.tridentsdk.server.world;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Arrays;

import static net.tridentsdk.server.net.NetData.wvint;

/**
 * Represents a 16x16x16 horizontal slab in a chunk column.
 */
@NotThreadSafe // TODO
public class ChunkSection {
    /**
     * The default amount of bits per palette index
     */
    private final int bitsPerBlock = 4;
    /**
     * The chunk section palette, containing the block
     * states
     */
    @GuardedBy("palette")
    private final ShortList palette = new ShortArrayList();
    /**
     * The data array, which contains palette indexes at
     * the XYZ index in the array
     */
    private final long[] data = new long[4096 >> this.bitsPerBlock];
    /**
     * The nibble array of light emitted from blocks
     */
    private final byte[] blockLight = new byte[2048];
    /**
     * The nibble array of light reaching from the sky
     */
    private final byte[] skyLight = new byte[2048];

    /**
     * Creates a new chunk section.
     */
    public ChunkSection() {
        this.palette.add((short) 0);
        Arrays.fill(this.data, 0L);
        Arrays.fill(this.blockLight, (byte) 0xFF);
        Arrays.fill(this.skyLight, (byte) 0xFF);
    }

    /**
     * Sets the block at the given position in the chunk
     * section to the given block state.
     *
     * @param idx the XYZ index
     * @param state the block state to set
     */
    public void set(int idx, short state) {
        int bitsPerBlock = this.bitsPerBlock;
        int paletteIdx;
        synchronized (this.palette) {
            paletteIdx = this.palette.indexOf(state);

            if (paletteIdx == -1) {
                this.palette.add(state);
                paletteIdx = this.palette.size() - 1;

                if (this.palette.size() > 1 << bitsPerBlock) {
                    // TODO Increase bits per block
                }
            }
        }

        int dataIdx = idx >> bitsPerBlock;
        int shift = (idx & 15) * bitsPerBlock;
        long or = ((long) paletteIdx) << shift;
        this.data[dataIdx] = this.data[dataIdx] | or;
    }

    /**
     * Writes the section data to the given byte stream.
     *
     * @param buf the buffer to write the section data
     */
    public void write(ByteBuf buf) {
        // Write Bits per block
        buf.writeByte(this.bitsPerBlock);

        // Write the palette size
        wvint(buf, this.palette.size());

        // Write the palette itself
        for (short s : this.palette) {
            wvint(buf, s);
        }

        // Write the section data length
        wvint(buf, this.data.length);

        // Write the actual data
        for (long l : this.data) {
            buf.writeLong(l);
        }

        // Write block light
        buf.writeBytes(this.blockLight);

        // Write skylight (only written if overworld)
        buf.writeBytes(this.skyLight); // TODO overworld
    }
}