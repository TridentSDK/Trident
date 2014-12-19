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
     * @return the height map for the chunk
     */
    abstract Map<ChunkLocation, Float> heightMap();

    /**
     * The tile to be set at the coordinates
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return the tile to be set at the coordinates
     */
    abstract ChunkTile atCoordinate(int x, int y, int z);

    public List<ChunkTile> doGen(World world) {
        List<ChunkTile> gen = Lists.newArrayList();
        for (Map.Entry<ChunkLocation, Float> e : this.heightMap().entrySet()) {
            ChunkLocation l = e.getKey();
            gen.add(atCoordinate(l.getX(), e.getValue().intValue(), l.getZ()));
        }

        return gen;
    }
}
