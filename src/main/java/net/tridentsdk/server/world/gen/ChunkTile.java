package net.tridentsdk.server.world.gen;

import net.tridentsdk.Coordinates;
import net.tridentsdk.base.Substance;
import net.tridentsdk.base.Tile;
import net.tridentsdk.world.World;

/**
 * Represents a tile that is awaiting pending status to be set in a generated world
 *
 * @author The TridentSDK Team
 */
public class ChunkTile {
    private final int x;
    private final int y;
    private final int z;
    private final Substance substance;
    private final byte data;

    private ChunkTile(int x, int y, int z, Substance substance, byte data) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.substance = substance;
        this.data = data;
    }

    /**
     * Creates a new pending chunk tile
     *
     * @param x x coordinate of the tile
     * @param y y coordinate of the tile
     * @param z z coordinate of the tile
     * @param substance the material to set at the location
     * @return the new pending chunk tile
     */
    public static ChunkTile create(int x, int y, int z, Substance substance) {
        return new ChunkTile(x, y, z, substance, (byte) 0);
    }

    public static ChunkTile create(int x, int y, int z, Substance substance, byte data) {
        return new ChunkTile(x, y, z, substance, data);
    }

    /**
     * Sets the block at the location in the specified world the pending tile
     *
     * @param world the world to set the block to
     */
    public void apply(World world) {
        // Executes after chunk is created by handler
        Tile tile = world.getTileAt(Coordinates.create(world, x, y, z));
        tile.setSubstance(substance);
        tile.setMeta(data);
    }
}
