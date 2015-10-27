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
package net.tridentsdk.server.world.gen.brush;

import net.tridentsdk.base.Substance;
import net.tridentsdk.server.world.WorldUtils;
import net.tridentsdk.world.ChunkLocation;
import net.tridentsdk.world.gen.FeatureGenerator;
import net.tridentsdk.world.gen.GeneratorRandom;

import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Creates tall grass in the world
 *
 * @author The TridentSDK Team
 */
public class TallGrassBrush extends FeatureGenerator {
    public TallGrassBrush(long seed) {
        super(seed);
    }

    @Override
    public void generate(ChunkLocation location, int relX, int relZ, GeneratorRandom random, AtomicReferenceArray<Integer> heights, ChunkManipulator manipulator) {
        int i = WorldUtils.heightIndex(relX, relZ);
        int top = heights.get(i);
        if (random.under(99) < 40 && manipulator.blockAt(relX, top, relZ).substance() == Substance.GRASS) {
            int y = top + 1;
            manipulator.manipulate(relX, y, relZ, Substance.LONG_GRASS, (byte) 0x01);
        }
    }
}