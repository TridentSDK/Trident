/*
 * Copyright (C) 2014 The TridentSDK Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tridentsdk.world;

import net.tridentsdk.api.Block;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.world.*;

import java.io.Serializable;
import java.util.*;

public class TridentWorld implements Serializable, World {
    private static final int               SIZE             = 1;
    private static final int               MAX_HEIGHT       = 255;
    private static final int               MAX_CHUNKS       = -1;
    private static final long              serialVersionUID = 2892463980167406259L;
    final                Collection<Chunk> chunks           = new ArrayList<>();
    private final String      name;
    private final Random      random;
    private final WorldLoader loader;
    public        Location    spawnLocation;

    TridentWorld(String name, WorldLoader loader) {
        this.name = name;
        this.loader = loader;
        this.random = new Random();

        // TODO Set spawn point
    }

    @Override public String getName() {
        return this.name;
    }

    @Override public Chunk getChunkAt(int x, int z, boolean generateIfNotFound) {
        if (this.chunks == null) {
            return null;
        }

        for (Chunk chunk : this.chunks.toArray(new Chunk[this.chunks.size()])) {
            if (chunk == null) {
                continue;
            }

            if ((chunk.getX() == x) && (chunk.getZ() == z)) {
                return chunk;
            }
        }

        if (generateIfNotFound) {
            this.generateChunk(x, z);
            return this.getChunkAt(x, z, false);
        } else {
            return null;
        }
    }

    @Override public void generateChunk(int x, int z) {
        if ((x > TridentWorld.MAX_CHUNKS) || (x < -TridentWorld.MAX_CHUNKS)) {
            return;
        }

        if ((z > TridentWorld.MAX_CHUNKS) || (z < -TridentWorld.MAX_CHUNKS)) {
            return;
        }

        if (this.getChunkAt(x, z, false) == null) {
            if (this.loader.chunkExists(this, x, z)) {
                this.chunks.add(this.loader.loadChunk(this, x, z));

                Chunk c = new TridentChunk(this, x, z);
                this.chunks.add(c);
                c.generate();
            }
        }
    }

    @Override
    public Block getBlockAt(Location location) {
        if (location.getWorld().getName().equals(this.getName()))
            throw new IllegalArgumentException("Provided location does not have the same world!");

        return null;
    }
}
