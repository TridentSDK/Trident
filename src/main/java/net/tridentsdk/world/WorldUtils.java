/*
 * Copyright (c) 2014, TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of TridentSDK nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
