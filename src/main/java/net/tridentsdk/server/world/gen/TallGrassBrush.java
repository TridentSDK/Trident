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
import net.tridentsdk.world.gen.AbstractOverlayBrush;
import net.tridentsdk.world.gen.GeneratorRandom;

/**
 * Creates tall grass in the world
 *
 * @author The TridentSDK Team
 */
public class TallGrassBrush extends AbstractOverlayBrush {
    public TallGrassBrush(long seed) {
        super(seed);
    }

    @Override
    public void brush(ChunkLocation location, int relX, int top, int relZ, GeneratorRandom random, ChunkManipulator manipulator) {
        if (random.under(99) > 90 /* && manipulator.blockAt(relX, top, relZ).substance() == Substance.GRASS */) {
            manipulator.manipulate(relX, top + 1, relZ, Substance.LONG_GRASS, (byte) 0x01);
        }
    }
}