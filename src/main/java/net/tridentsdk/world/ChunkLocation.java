/*
 *     TridentSDK - A Minecraft Server API
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.world;

import java.io.Serializable;

/**
 * Stores the location of a Chunk
 *
 * @author drew
 */
public class ChunkLocation implements Serializable, Cloneable {
    private static final long serialVersionUID = 9083698035337137603L;
    private int x;
    private int z;

    public ChunkLocation(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public ChunkLocation(ChunkLocation coord) {
        this.x = coord.getX();
        this.z = coord.getZ();
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return this.z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public Chunk getChunk() {
        return null;
    }
}
