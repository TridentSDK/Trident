package net.tridentsdk.world;

import net.tridentsdk.api.world.ChunkLocation;

public class WorldUtils {

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
        return "r." + (location.getX() >> 5) + "." + (location.getZ() >> 5) + ".mca";
    }

    /**
     * Gets a region file name for a given x and y location in the world
     * @param x
     * @param z
     * @return
     */
    public static String getRegionFile (int x, int z) {
        return "r." + (x >> 9) + "." + (z >> 9) + ".mca";
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

}
