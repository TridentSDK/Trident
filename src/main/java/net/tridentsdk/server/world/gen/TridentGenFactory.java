/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
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

import net.tridentsdk.factory.GenFactory;
import net.tridentsdk.server.world.ChunkSection;
import net.tridentsdk.server.world.TridentChunk;
import net.tridentsdk.server.world.WorldUtils;
import net.tridentsdk.util.NibbleArray;
import net.tridentsdk.world.Chunk;
import net.tridentsdk.world.gen.TempGenBlock;

import java.util.Arrays;

/**
 * Implementation of generation factory, used to produce effects of world generation implementation side
 *
 * @author The TridentSDK Team
 */
public class TridentGenFactory implements GenFactory {
    @Override
    public void putBlock(TempGenBlock block, Chunk chunk) {
        int blockX = (int) block.coordinates().x();
        int blockY = (int) block.coordinates().y();
        int blockZ = (int) block.coordinates().z();

        int index = WorldUtils.blockArrayIndex(blockX % 16, blockY, blockZ % 16);
        int sectionId = WorldUtils.section(blockY);

        TridentChunk tChunk = (TridentChunk) chunk;
        if (tChunk.sections == null) tChunk.sections = new ChunkSection[16];

        for (int i = 0; i < tChunk.sections.length; i++) {
            ChunkSection sect = null;
            if (tChunk.sections[i] == null)
                sect = tChunk.sections[i] = new ChunkSection();

            // Already set
            if (sect == null) continue;
            sect.rawTypes = new byte[4096];
            sect.types = new char[4096];
            sect.blockLight = new byte[2048];
            sect.skyLight = new byte[2048];

            Arrays.fill(sect.skyLight, (byte) 15);

            sect.add = new byte[2048];
            sect.data = new byte[2048];
        }

        ChunkSection section = tChunk.sections[sectionId];

        byte b = section.rawTypes[index] = (byte) block.substance().id();
        NibbleArray.set(section.add, index, (byte) 0);
        NibbleArray.set(section.data, index, block.meta());

        section.types[index] = (char) (((b & 0xff) << 4) | block.meta());
        section.blockLight[index] = 16;
        section.skyLight[index] = 16;
    }
}
