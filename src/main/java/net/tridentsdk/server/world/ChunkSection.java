/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
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
import net.tridentsdk.server.util.NibbleArray;
import net.tridentsdk.server.util.ShortArrayList;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.atomic.AtomicLongArray;

import static net.tridentsdk.server.net.NetData.wvint;

/**
 * Represents a 16x16x16 horizontal slab in a chunk column.
 */
@ThreadSafe
public class ChunkSection {
    /**
     * Empty chunk section for writing overworld chunks
     */
    public static final ChunkSection EMPTY_WITH_SKYLIGHT = new ChunkSection(true);
    /**
     * Empty chunk section which does not write skylight
     */
    public static final ChunkSection EMPTY_WITHOUT_SKYLIGHT = new ChunkSection(false);
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
    private final AtomicLongArray data = new AtomicLongArray((BLOCKS_PER_SECTION * this.bitsPerBlock) / 64);
    /**
     * The nibble array of light emitted from blocks
     */
    private final NibbleArray blockLight = new NibbleArray(BLOCKS_PER_SECTION / 2);
    /**
     * The nibble array of light reaching from the sky
     */
    private final NibbleArray skyLight = new NibbleArray(BLOCKS_PER_SECTION / 2);
    /**
     * The flag for writing skylight in other dimensions
     */
    private final boolean doSkylight;

    /**
     * Creates a new chunk section.
     */
    public ChunkSection(boolean doSkylight) {
        // Unsynchronized write is ok because we write final
        // at the end of construction
        this.palette.add((short) 0);

        for (int i = 0; i < this.data.length(); i++) {
            this.data.set(i, 0L);
        }

        this.blockLight.fill((byte) 0xF);
        this.skyLight.fill((byte) 0xF);
        this.doSkylight = doSkylight;
    }

    /**
     * Sets the block at the given position in the chunk
     * section to the given block getState.
     *
     * @param idx the XYZ index
     * @param state the block getState to set
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

        int dataIdx = idx * bitsPerBlock / 64;
        int shift = idx % (64 / bitsPerBlock) * bitsPerBlock;
        long or = (long) paletteIdx << shift;
        long and = ~((~((long) paletteIdx) & (1 << bitsPerBlock) - 1) << shift);

        long oldLong;
        long newLong;
        do {
            oldLong = this.data.get(dataIdx);
            newLong = (oldLong & and) | or;
        }
        while (!this.data.compareAndSet(dataIdx, oldLong, newLong));
        // TODO relighting
    }

    /**
     * Obtains the data for a block contained in this chunk
     * section with the given position.
     *
     * @param idx The position of the block
     * @return A tuple consisting of substance and meta
     */
    public short dataAt(int idx) {
        int dataIdx = idx * this.bitsPerBlock / 64;
        int shift = idx % (64 / this.bitsPerBlock) * this.bitsPerBlock;
        long paletteIdx = (this.data.get(dataIdx) >> shift) & (1 << this.bitsPerBlock) - 1;

        synchronized (this.palette) {
            return this.palette.getShort((int) paletteIdx);
        }
    }

    /**
     * Writes the section data to the given byte stream.
     *
     * @param buf the buffer to write the section data
     */
    public void write(ByteBuf buf) {
        // Write Bits per block
        buf.writeByte(this.bitsPerBlock);

        // Cache the palette in order to prevent breaking
        // the packet with a concurrent write
        ShortArrayList palette;
        synchronized (this.palette) {
            palette = this.palette;
        }

        // Write the palette size
        wvint(buf, this.palette.size());

        for (int i = 0, lim = palette.size(); i < lim; i++) {
            // range check is actually simple if statement,
            // we like that over iterators so this is the
            // preference iteration method
            wvint(buf, palette.getShort(i));
        }

        // Write the section data length
        int dataLen = this.data.length();
        wvint(buf, dataLen);

        // Write the actual data
        for (int i = 0; i < dataLen; i++) {
            buf.writeLong(this.data.get(i));
        }

        // Write block light
        this.blockLight.write(buf);

        // Write skylight (only written if overworld)
        if (this.doSkylight) {
            this.skyLight.write(buf);
        }
    }
}