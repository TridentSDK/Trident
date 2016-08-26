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
package net.tridentsdk.server.world.opt;

import gnu.trove.TCollections;
import gnu.trove.list.TCharList;
import gnu.trove.list.array.TCharArrayList;
import io.netty.buffer.ByteBuf;
import net.tridentsdk.world.opt.BlockState;

import java.util.Arrays;

import static net.tridentsdk.server.net.NetData.wvint;

/**
 * Represents a 16x16x16 horizontal slab in a chunk column.
 */
public class ChunkSection {

    private int BITS_PER_BLOCK = 4;
    private final int secY;
    private final TCharList palette = TCollections.synchronizedList(new TCharArrayList());
    private final long[] data = new long[4096 >> BITS_PER_BLOCK];
    private final byte[] blockLight = new byte[2048];
    private final byte[] skyLight = new byte[2048];

    public ChunkSection(int secY) {
        this.secY = secY;
        this.palette.add((char) 0);
        Arrays.fill(this.data, 0L);
        Arrays.fill(this.blockLight, (byte) 0x00);
        Arrays.fill(this.skyLight, (byte) 0x00);
    }

    public void set(int idx, BlockState state) {
        int paletteIdx = this.palette.indexOf(state.toChar());

        if (paletteIdx == -1) {
            this.palette.add(state.toChar());
            paletteIdx = this.palette.size() - 1;
            System.out.println(state + " NOT IN PALETTE, INSERTED AT " + paletteIdx);

            if(this.palette.size() > 1 << BITS_PER_BLOCK){
                // TODO Increase bits per block
            }
        }

        int dataIdx = idx >> BITS_PER_BLOCK;
        int shift = (idx & 15) * BITS_PER_BLOCK;
        long or = ((long) paletteIdx) << shift;
        this.data[dataIdx] = this.data[dataIdx] | or;
    }

    public void write(ByteBuf buf) {
        // Write Bits per block
        buf.writeByte(BITS_PER_BLOCK);

        // Write the palette size
        wvint(buf, this.palette.size());

        // Write the palette itself
        this.palette.forEach(value -> {
            wvint(buf, value);
            return true;
        });

        // Write the section data length
        wvint(buf, this.data.length);

        // Write the actual data
        for (long l : this.data) {
            buf.writeLong(l);
        }

        // Write block light
        buf.writeBytes(this.blockLight);

        // Write skylight (only written if overworld)
        //buf.writeBytes(this.skyLight); // TODO overworld
    }
}