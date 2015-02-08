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
import net.tridentsdk.world.ChunkLocation;
import net.tridentsdk.world.gen.AbstractGenerator;
import net.tridentsdk.world.gen.TempGenBlock;

/**
 * Default world generator engine for Trident
 *
 * @author The TridentSDK Team
 */
public class DefaultWorldGen extends AbstractGenerator {
    private final PerlinNoise noise = new PerlinNoise(16, 256);

    @Override
    public int height(int x, int z) {
        return (int) noise.noise(x, z);
    }

    @Override
    public TempGenBlock atCoordinate(int x, int y, int z) {
        return TempGenBlock.create(x, y, z, Substance.GRASS);
    }

    @Override
    public byte[][] generateBlockData(ChunkLocation location) {
        return new byte[0][];
    }

    @Override
    public char[][] generateChunkBlocks(ChunkLocation location) {
        return new char[0][];
    }
}
