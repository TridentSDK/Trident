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

import java.util.Random;

/**
 * Default world generator engine for Trident
 *
 * @author The TridentSDK Team
 */
public class DefaultWorldGen extends AbstractGenerator {
    //private final PerlinNoise noise = new PerlinNoise(16, 256);
    private final SimplexOctaveGenerator generator = new SimplexOctaveGenerator(8, 0.5, new Random().nextInt());

    @Override
    public char[][] generateChunkBlocks(ChunkLocation location) {
        char[][] data = new char[15][ChunkSection.LENGTH];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                final int i = WorldUtils.intScale(0, 140, generator.noise(x + (location.x() << 4), z + (location.z() << 4)))-20;
                for (int y = 0; y < i; y++ ) {
                    //System.out.println(y);
                    if(i < 40 && y == (i - 1)) {
                        for (int rev = 40; rev > i; rev--) {
                            data[rev/16][WorldUtils.blockArrayIndex(x,rev%16,z)] = Substance.WATER.asExtended();
                        }
                        data[i/16][WorldUtils.blockArrayIndex(x,i%16,z)] = Substance.CLAY.asExtended();
                    }
                    
                    data[y/16][WorldUtils.blockArrayIndex(x,y%16,z)] = Substance.GRASS.asExtended();
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
