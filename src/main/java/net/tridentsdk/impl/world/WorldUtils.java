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
package net.tridentsdk.impl.world;

import net.tridentsdk.api.world.ChunkLocation;

public final class WorldUtils {

    public static final byte NIBBLE_MASK = (byte) 0x0F;

    private WorldUtils() {
    }

    /**
     * Get section index from y height
     *
     * @param y the y height specified
     * @return the section index
     */
    public static int getSection(int y) {
        return y >> 4;
    }

    /**
     * Gets the chunk location from a position X and Y
     *
     * @param x the specified x
     * @param z the specified z
     * @return the chunk location
     */
    public static ChunkLocation getChunkLocation(int x, int z) {
        return new ChunkLocation(x >> 4, z >> 4);
    }

    /**
     * Gets the name of a region file for a given chunklocation
     *
     * @param location the location to get the region file for
     * @return the region file containing the location
     */
    public static String getRegionFile(ChunkLocation location) {
        return "r." + (location.getX() >> 5) + '.' + (location.getZ() >> 5) + ".mca";
    }

    /**
     * Gets a region file name for a given x and y location in the world
     *
     * @param x the specified x
     * @param z the specified z
     * @return the region file containing the x and y
     */
    public static String getRegionFile(int x, int z) {
        return "r." + (x >> 9) + '.' + (z >> 9) + ".mca";
    }

    /**
     * Gets the index of a block in a section
     *
     * @param x the specified x
     * @param y the y height specified
     * @param z the specified z
     * @return the index of the block array containing the coordinates given
     */
    public static int getBlockArrayIndex(int x, int y, int z) {
        return y << 8 + z << 4 + x;
    }

    /**
     * The byte world nibble array byte search
     *
     * @param nibbleArray the nibble array to search from
     * @param index       the nibble index
     * @return the index of the nibble byte
     */
    public static byte getFromNibbleArray(byte[] nibbleArray, int index) {
        boolean off = index % 2 == 1;

        if (off) {
            return (byte) (nibbleArray[index / 2] >>> 4);
        } else {
            return (byte) (nibbleArray[index / 2] & NIBBLE_MASK);
        }
    }

}
