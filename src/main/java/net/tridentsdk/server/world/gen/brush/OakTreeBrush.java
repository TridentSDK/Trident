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
import net.tridentsdk.world.ChunkLocation;
import net.tridentsdk.world.gen.AbstractOverlayBrush;
import net.tridentsdk.world.gen.GeneratorRandom;

/**
 * Generates oak trees in the world
 *
 * @author The TridentSDK Team
 */
public class OakTreeBrush extends AbstractOverlayBrush {
    public OakTreeBrush(long seed) {
        super(seed);
    }

    @Override
    public void brush(ChunkLocation location, int relX, int top, int relZ, GeneratorRandom random, ChunkManipulator manipulator) {
        Substance substance = manipulator.blockAt(relX, top, relZ).substance();
        if (random.under(100) < 1 && (substance == Substance.GRASS || substance == Substance.DIRT)) {
            for (int i = 1; i < 7; i++) {
                if (i <= 2) {
                    manipulator.manipulate(relX, top + i, relZ, Substance.LOG, (byte) 0x00);
                } else if (i > 2 && i <= 4) {
                    manipulator.manipulate(relX, top + i, relZ, Substance.LOG, (byte) 0x00);
                    /*
                     -2, 2 L L L L L 2,2
                           L L L L L
                           L L O L L       L = Leaves
                           L L L L L       O = Oak
                     -2,-2 L L L L L 2,-2
                     */

                    for (int x = relX - 2; x <= relX + 2; x++) {
                        for (int z = relZ - 2; z <= relZ + 2; z++) {
                            if (x == relX && z == relZ) {
                                manipulator.manipulate(relX, top + i, relZ, Substance.LOG, (byte) 0x00);
                            } else {
                                manipulator.manipulate(x, top + i, z, Substance.LEAVES, (byte) 0x00);
                            }
                        }
                    }
                } else if (i > 4) {
                    if (i != 6) {
                        manipulator.manipulate(relX, top + i, relZ, Substance.LOG, (byte) 0x00);
                    } else {
                        manipulator.manipulate(relX, top + i, relZ, Substance.LEAVES, (byte) 0x00);
                    }
                    manipulator.manipulate(relX + 1, top + i, relZ, Substance.LEAVES, (byte) 0x00);
                    manipulator.manipulate(relX - 1, top + i, relZ, Substance.LEAVES, (byte) 0x00);
                    manipulator.manipulate(relX, top + i, relZ + 1, Substance.LEAVES, (byte) 0x00);
                    manipulator.manipulate(relX, top + i, relZ - 1, Substance.LEAVES, (byte) 0x00);
                }
            }
        }
    }
}
