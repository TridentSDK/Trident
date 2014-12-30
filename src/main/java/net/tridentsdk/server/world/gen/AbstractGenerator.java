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

import com.google.common.collect.Lists;
import net.tridentsdk.world.ChunkLocation;
import net.tridentsdk.world.World;

import java.util.List;
import java.util.Map;

/**
 * The base class for implementing world generation extensions
 *
 * @author The TridentSDK Team
 */
public abstract class AbstractGenerator {
    /**
     * Where ChunkLocation is the x/z of the block for the height to be specified in the value
     *
     * @param x the x coordinate to find the height
     * @param z the z coordinate to find the height
     * @return the height at that coordinate
     */
    public abstract int height(int x, int z);

    /**
     * The tile to be set at the coordinates
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return the tile to be set at the coordinates
     */
    public abstract ChunkTile atCoordinate(int x, int y, int z);

    public List<ChunkTile> doGen(ChunkLocation corner1, ChunkLocation corner2) {
        List<ChunkTile> gen = Lists.newArrayList();
        for (int x = corner1.getX(); x < corner2.getZ(); x++) {
            for (int z = corner1.getZ(); z < corner2.getZ(); z++) {
                int height = height(x, z);

                gen.add(atCoordinate(x, height, z));
                for (int i = height; i > 0; i--) {
                    gen.add(atCoordinate(x, i, z));
                }
            }
        }

        return gen;
    }
}
