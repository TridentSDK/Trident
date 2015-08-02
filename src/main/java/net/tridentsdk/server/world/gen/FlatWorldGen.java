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

import net.tridentsdk.base.Substance;
import net.tridentsdk.server.world.ChunkSection;
import net.tridentsdk.server.world.WorldUtils;
import net.tridentsdk.world.ChunkLocation;
import net.tridentsdk.world.gen.AbstractGenerator;

import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Generates a flat world
 *
 * @author The TridentSDK Team
 */
public class FlatWorldGen extends AbstractGenerator {
    public FlatWorldGen(long seed) {
        super(seed);
    }

    @Override
    public char[][] generateChunkBlocks(ChunkLocation location, AtomicReferenceArray<Integer> heights) {
        char[][] data = new char[1][ChunkSection.LENGTH];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                heights.set(WorldUtils.heightIndex(x, z), 3);

                for (int y = 0; y < 4; y++ ) {
                    switch (y) {
                        case 0:
                            data[0][WorldUtils.blockArrayIndex(x,y,z)] = Substance.BEDROCK.asExtended();
                            break;
                        case 1:
                            // fall through
                        case 2:
                            data[0][WorldUtils.blockArrayIndex(x,y,z)] = Substance.DIRT.asExtended();
                            break;
                        case 3:
                            data[0][WorldUtils.blockArrayIndex(x,y,z)] = Substance.GRASS.asExtended();
                            break;
                    }

                }
            }
        }
        return data;
    }

    @Override
    public byte[][] generateBlockData(ChunkLocation location) {
        return new byte[0][];
    }
}
