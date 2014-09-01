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

import net.tridentsdk.api.world.Chunk;

import java.io.Serializable;
import java.util.Random;

public class TridentChunk implements Serializable, Chunk {

    private static final long serialVersionUID = 3323137810332318805L;
    public final  TridentWorld world;
    private final int          x;
    private final int          z;

    public TridentChunk(TridentWorld world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    @Override public void generate() {
        int chunkX = this.x * 16;
        int chunkZ = this.z * 16;

        Random r = new Random();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                //TODO y

                int y = 0;

                //TODO Place blocks
            }
        }
    }

    @Override public int getX() {
        return this.x;
    }

    @Override public int getZ() {
        return this.z;
    }

    @Override public TridentWorld getWorld() {
        return this.world;
    }
}
