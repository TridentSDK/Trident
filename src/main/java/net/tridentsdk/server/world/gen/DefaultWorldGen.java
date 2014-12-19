package net.tridentsdk.server.world.gen;

import com.google.common.collect.Maps;
import net.tridentsdk.base.Substance;
import net.tridentsdk.world.ChunkLocation;

import java.util.Map;

/**
 * Default world generator engine for Trident
 *
 * @author The TridentSDK Team
 */
public class DefaultWorldGen extends AbstractGenerator {
    @Override
    public Map<ChunkLocation, Float> heightMap() {
        int size = 16;
        Map<ChunkLocation, Float> map = Maps.newHashMap();
        PerlinNoise noise = new PerlinNoise(size, 256);

        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                map.put(ChunkLocation.create(x, z), noise.noise(x, z));
            }
        }

        return map;
    }

    @Override
    public ChunkTile atCoordinate(int x, int y, int z) {
        return ChunkTile.create(x, y, z, Substance.GRASS);
    }
}
