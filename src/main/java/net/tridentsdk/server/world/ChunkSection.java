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
     * The amount of blocks in a chunk section
     */
    private static final int BLOCKS_PER_SECTION = 4096;

    /**
     * The default amount of bits per palette index
     */
    private final int bitsPerBlock = 4;
    /**
     * The chunk section palette, containing the block
     * states
     */
    @GuardedBy("palette")
    private final ShortArrayList palette = new ShortArrayList();
    /**
     * The data array, which contains palette indexes at
     * the XYZ index in the array
     */
    private final long[] data = new long[(BLOCKS_PER_SECTION * this.bitsPerBlock) / 64];
    /**
     * The nibble array of light emitted from blocks
     */
    private final byte[] blockLight = new byte[BLOCKS_PER_SECTION / 2];
    /**
     * The nibble array of light reaching from the sky
     */
    private final byte[] skyLight = new byte[BLOCKS_PER_SECTION / 2];

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

        int dataIdx = (idx * bitsPerBlock) / 64;
        int shift = (idx & ((64 / bitsPerBlock) - 1)) * bitsPerBlock;
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
        ShortArrayList palette;
        synchronized (this.palette) {
            palette = this.palette;
        }

        for (int i = 0, lim = palette.size(); i < lim; i++) {
            // range check is actually simple if statement,
            // we like that over iterators so this is the
            // preference iteration method
            wvint(buf, palette.getShort(i));
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