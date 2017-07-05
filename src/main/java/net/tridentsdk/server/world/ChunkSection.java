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
import net.tridentsdk.meta.nbt.Tag;
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
    private final AtomicLongArray data = new AtomicLongArray(BLOCKS_PER_SECTION / SHORTS_PER_LONG);
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
            if (bitsPerBlock < 4) {
                bitsPerBlock = 4;
            }

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
                        short shortData = (short) data;
                        boolean added = this.mainPalette.add(shortData);

                        if (doPalette) {
                            if (added) {
                                data = palette.add(shortData);
                            } else {
                                data = palette.indexOf(shortData);
                                if (data == -1) {
                                    throw new IllegalStateException("Failed to lock");
                                }
                            }
                        }

                        int shift = realIdx * bitsPerBlock % 64;
                        long or = data & individualValueMask;
                        bitsWritten += bitsPerBlock;

                        if (bitsWritten == 64) {
                            dataLen++;
                            dataBuffer.writeLong(cur | or << shift);

                            cur = 0;
                            bitsWritten = 0;
                        } else if (bitsWritten > 64) {
                            bitsWritten -= 64;
                            int lowerLen = bitsPerBlock - bitsWritten;
                            int lowerMask = (1 << lowerLen) - 1;

                            dataLen++;
                            dataBuffer.writeLong(cur | (or & lowerMask) << shift);

                            cur = (or & ~lowerMask) >> lowerLen;
                        } else {
                            cur |= or << shift;
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

        if (dataBuffer != null) {
            dataBuffer.release();
        }

        // Write block light
        this.blockLight.write(buf);

        // Write skylight (only written if overworld)
        if (this.doSkylight) {
            this.skyLight.write(buf);
        }
    }

    /**
     * Loads block data from the NBT tag read at the chunk's
     * region file into this chunk section.
     *
     * @param section the section to load NBT data
     */
    public void read(Tag.Compound section) {
        byte[] blocks = section.getByteArray("Blocks");
        byte[] add = section.get("Add");
        byte[] data = section.getByteArray("Data");
        byte[] skyLight = section.getByteArray("SkyLight");
        byte[] blockLight = section.getByteArray("BlockLight");

        this.skyLight.read(skyLight);
        this.blockLight.read(blockLight);

        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    int realIdx = y << 8 | z << 4 | x;

                    int block = blocks[realIdx];
                    byte blockData = NibbleArray.getNibble(data, realIdx);
                    if (add != null) {
                        int blockId = block + ((int) NibbleArray.getNibble(add, realIdx) << 8);
                        short state = (short) (blockId << 4 | blockData);
                        this.set(realIdx, state);
                    } else {
                        short state = (short) (block << 4 | blockData);
                        this.set(realIdx, state);
                    }
                }
            }
        }
    }

    /**
     * Writes the data from this chunk section into the
     * given NBT data going into a chunk's {@code Sections}
     * list.
     *
     * @param section the section to write
     */
    public void write(Tag.Compound section) {
        section.putByteArray("SkyLight", this.skyLight.write());
        section.putByteArray("BlockLight", this.skyLight.write());

        byte[] blocks = new byte[4096];
        byte[] add = new byte[2048];
        byte[] data = new byte[2048];
        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    int realIdx = y << 8 | z << 4 | x;
                    short state = this.dataAt(realIdx);
                    int blockId = state >> 4;

                    blocks[realIdx] = (byte) (blockId & 0xFF);
                    NibbleArray.setNibble(data, realIdx, (byte) (state & 0xF));
                    if (blockId > 255) {
                        NibbleArray.setNibble(add, realIdx, (byte) (blockId >> 8));
                    }
                }
            }
        }

        section.putByteArray("Blocks", blocks);
        section.putByteArray("Add", add);
        section.putByteArray("Data", data);
    }
}