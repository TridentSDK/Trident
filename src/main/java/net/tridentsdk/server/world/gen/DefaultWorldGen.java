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

import com.google.common.collect.Maps;
import net.tridentsdk.base.Substance;
import net.tridentsdk.world.ChunkLocation;

import java.util.Map;

/**
 * Default world generator engine for Trident
 *
 * @author The TridentSDK Team
 */
public class DefaultWorldGen extends AbstractGenerator {
    @Override
    public Map<ChunkLocation, Float> heightMap() {
        int size = 16;
        Map<ChunkLocation, Float> map = Maps.newHashMap();
        PerlinNoise noise = new PerlinNoise(size, 256);

        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                map.put(ChunkLocation.create(x, z), noise.noise(x, z));
            }
        }

        return map;
    }

    @Override
    public ChunkTile atCoordinate(int x, int y, int z) {
        return ChunkTile.create(x, y, z, Substance.GRASS);
    }
}
