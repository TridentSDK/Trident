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
package net.tridentsdk.server.world.gen;

import net.tridentsdk.world.gen.GeneratorContext;

/**
 * Implementation of a generator context
 */
public class GeneratorContextImpl implements GeneratorContext {
    private long seed;

    public GeneratorContextImpl() {
        this(System.nanoTime());
    }

    public GeneratorContextImpl(long seed) {
        // TODO 48 bit seed???
        if (seed == 0) {
            seed = System.nanoTime();
        }

        this.seed = seed;
    }

    @Override
    public long nextLong() {
        return 0;
    }

    @Override
    public long nextLong(long max) {
        long x = this.seed;
        x ^= (x << 21);
        x ^= (x >>> 35);
        x ^= (x << 4);
        return x;
    }

    @Override
    public int nextInt() {
        return 0;
    }

    @Override
    public int nextInt(int max) {
        return 0;
    }

    @Override
    public long seed() {
        return 0;
    }

    // char is perfect for storing block data because
    // char = 2 bytes = 16 bits
    // 8 bit block id
    // 4 bit meta
    // 4 bit add (unused)
    // ------------------
    // 16 bits

    public static void main(String[] args) {
        char block = 0;

        char idc = 253;
        char metac = 0x2;

        block |= idc << 8;
        block |= metac << 4;

        int blockMask = 0xff00;
        int metaMask = 0xf0;
        int addMask = 0xf;

        System.out.println((int) block);
        System.out.println((block & metaMask) >> 4);
        System.out.println((block & blockMask) >> 8);

        System.out.println();
    }

    @Override
    public char build(int id, byte meta) {


        return 0;
    }

    /**
     * http://minecraft.gamepedia.com/Chunk_format
     * int BlockPos = y*16*16 + z*16 + x;
     *
     * return (y * (2^8)) + (z * (2^4)) + x;
     * use OR instead because bitwise ops are faster and
     * provides the same results as addition
     *
     * max size of this array is blocks in section, 4096
     * 16*16*16
     */
    @Override
    public int idx(int x, int y, int z) {
        return y << 8 | z << 4 | x;
    }

    @Override
    public int section(int y) {
        return y & 15;
    }

    @Override
    public void set(int x, int y, int z, char block) {
        int index = this.idx(x, y, z);
        int bitsPerEntry = 8;

        int bitIndex = index * bitsPerEntry;
        int startIndex = bitIndex / 64;
        int endIndex = ((index + 1) * bitsPerEntry - 1) / 64;
        int startBitSubIndex = bitIndex % 64;
        /* this.data[startIndex] = this.data[startIndex] & ~(this.maxEntryValue << startBitSubIndex) | ((long) value & this.maxEntryValue) << startBitSubIndex;
        if(startIndex != endIndex) {
            int endBitSubIndex = 64 - startBitSubIndex;
            this.data[endIndex] = this.data[endIndex] >>> endBitSubIndex << endBitSubIndex | ((long) value & this.maxEntryValue) >> endBitSubIndex;
        } */
    }

    @Override
    public char[][] data() {
        return new char[0][];
    }
}
