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
import net.tridentsdk.world.gen.TerrainGenerator;

import javax.annotation.concurrent.Immutable;

/**
 * This generator generates the base chunk layer used for
 * generating flat worlds.
 */
@Immutable
public class FlatTerrainGenerator implements TerrainGenerator {
    @Override
    public void generate(int chunkX, int chunkZ, GeneratorContext context) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                final int finalX = x;
                final int finalZ = z;
                context.run(() -> {
                    context.set(finalX, 0, finalZ, 7, (byte) 0);
                    context.set(finalX, 1, finalZ, 3, (byte) 0);
                    context.set(finalX, 2, finalZ, 3, (byte) 0);
                    context.set(finalX, 3, finalZ, 2, (byte) 0);
                });
            }
        }
    }
}