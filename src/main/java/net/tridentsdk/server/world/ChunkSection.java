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
import net.tridentsdk.server.util.ShortOpenHashSet;

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
     * The number of block state values that can be stored
     * in a single long
     */
    private static final int SHORTS_PER_LONG = 4;

    /**
     * The palette that caches the block states used by this
     * ChunkSection and evicts unused entries when the
     * chunk is written.
     */
    @GuardedBy("mainPalette")
    private final ShortOpenHashSet mainPalette = new ShortOpenHashSet();
    /**
     * The data array, which contains palette indexes at
     * the XYZ index in the array
     */
    private final AtomicLongArray data = new AtomicLongArray(1024);
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
        this.mainPalette.add((short) 0);
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
        synchronized (this.mainPalette) {
            this.mainPalette.add(state);
        }

        int spliceIdx = idx >>> 2;
        long shift = idx % SHORTS_PER_LONG << 4;

        long placeMask = ~(0xFFFFL << shift);
        long shiftedState = (long) state << shift;

        long oldSplice;
        long newSplice;
        do {
            oldSplice = this.data.get(spliceIdx);
            newSplice = oldSplice & placeMask | shiftedState;
        } while (!this.data.compareAndSet(spliceIdx, oldSplice, newSplice));
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
        int spliceIdx = idx >>> 2;
        long shift = idx % SHORTS_PER_LONG << 4;
        return (short) (this.data.get(spliceIdx) >>> shift & 0xFFFF);
    }

    /**
     * Writes the section data to the given byte stream.
     *
     * @param buf the buffer to write the section data
     */
    public void write(ByteBuf buf) {
        int dataLen = 0;
        ByteBuf dataBuffer = buf.alloc().buffer();

        ShortArrayList palette = null;
        int bitsPerBlock;
        boolean doPalette;
        synchronized (this.mainPalette) {
            int paletteSize = this.mainPalette.size();
            bitsPerBlock = Integer.highestOneBit(paletteSize);
            doPalette = bitsPerBlock < 9;

            this.mainPalette.clear();
            this.mainPalette.add((short) 0);

            if (!doPalette) {
                bitsPerBlock = 13;
            } else {
                palette = new ShortArrayList(paletteSize);
                palette.add((short) 0);
            }

            int individualValueMask = (1 << bitsPerBlock) - 1;
            int bitsWritten = 0;
            long cur = 0;
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        int realIdx = y << 8 | z << 4 | x;
                        int data = this.dataAt(realIdx);
                        boolean added = this.mainPalette.add((short) data);

                        if (doPalette) {
                            if (added) {
                                data = palette.add((short) data);
                            } else {
                                data = palette.indexOf((short) data);
                                if (data == -1) {
                                    throw new IllegalStateException("Failed to lock");
                                }
                            }
                        }

                        long shift = realIdx * bitsPerBlock % 64;
                        long or = (long) (data & individualValueMask) << shift;
                        bitsWritten += bitsPerBlock;

                        if (bitsWritten == 64) {
                            dataLen++;
                            dataBuffer.writeLong(cur | or);

                            cur = 0;
                            bitsWritten = 0;
                        } else if (bitsWritten > 64) {
                            int lowerMask = (1 << bitsPerBlock - (bitsWritten - 64)) - 1;
                            dataLen++;
                            dataBuffer.writeLong(cur | or & lowerMask);

                            cur = or & ~lowerMask;
                            bitsWritten = bitsWritten - 64;
                        } else {
                            cur |= or;
                        }
                    }
                }
            }
        }

        // Write Bits per block
        buf.writeByte(bitsPerBlock);

        // Write the palette size
        wvint(buf, doPalette ? palette.size() : 0);

        // Write palette
        for (int i = 0, max = doPalette ? palette.size() : 0; i < max; i++) {
            wvint(buf, palette.getShort(i));
        }

        // Write the section data length
        wvint(buf, dataLen);

        // Write the actual data
        buf.writeBytes(dataBuffer);
        dataBuffer.release();

        // Write block light
        this.blockLight.write(buf);

        // Write skylight (only written if overworld)
        if (this.doSkylight) {
            this.skyLight.write(buf);
        }
    }
}