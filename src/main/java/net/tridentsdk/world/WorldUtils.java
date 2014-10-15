/*
 *     Trident - A Multithreaded Server Alternative
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

import net.tridentsdk.api.world.ChunkLocation;

public final class WorldUtils {

    public static final byte NIBBLE_MASK = (byte)0x0F;

    private WorldUtils() {}

    /**
     * Get section index from y height
     * @param y
     * @return
     */
    public static int getSection (int y) {
        return y >> 4;
    }

    /**
     * Gets the chunk location from a position X and Y
     * @param x
     * @param z
     * @return
     */
    public static ChunkLocation getChunkLocation(int x, int z) {
        return new ChunkLocation( x >> 4, z >> 4);
    }

    /**
     * Gets the name of a region file for a given chunklocation
     * @param location
     * @return
     */
    public static String getRegionFile (ChunkLocation location) {
        return "r." + (location.getX() >> 5) + '.' + (location.getZ() >> 5) + ".mca";
    }

    /**
     * Gets a region file name for a given x and y location in the world
     * @param x
     * @param z
     * @return
     */
    public static String getRegionFile (int x, int z) {
        return "r." + (x >> 9) + '.' + (z >> 9) + ".mca";
    }

    /**
     * Gets the index of a block in a section
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static int getBlockArrayIndex(int x, int y, int z) {
        return y << 8 + z << 4 + x;
    }

    public static byte getFromNibbleArray(byte[] nibbleArray, int index) {
        boolean off = index % 2 == 1;

        if(off) {
            return (byte) (nibbleArray[index/2] >>> 4);
        } else {
            return (byte) (nibbleArray[index/2]& WorldUtils.NIBBLE_MASK);
        }
    }

}
