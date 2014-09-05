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
import net.tridentsdk.api.world.ChunkLocation;

import java.io.Serializable;
import java.util.Random;

public class TridentChunk implements Serializable, Chunk {

    private static final long serialVersionUID = 3323137810332318805L;
    private final  TridentWorld world;
    private final  ChunkLocation location; 

    public TridentChunk(TridentWorld world, int x, int z) {
       this(world, new ChunkLocation(x, z));
    }
    
    public TridentChunk(TridentWorld world, ChunkLocation coord) {
		this.world = world;
		this.location = coord;
	}

    @Override public void generate() {
        int chunkX = this.getX() * 16;
        int chunkZ = this.getZ() * 16;

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
        return this.location.getX();
    }

    @Override public int getZ() {
        return this.location.getX();
    }
    
	@Override
	public ChunkLocation getLocation() {
		return location;
	}

    @Override public TridentWorld getWorld() {
        return this.world;
    }



	
}
