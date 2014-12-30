package net.tridentsdk.server.world.gen;

import net.tridentsdk.base.Substance;
import net.tridentsdk.util.TridentLogger;

/**
 * Generates a flat world using
 */
public class FlatWorldGen extends AbstractGenerator {
    @Override
    public int height(int x, int z) {
        return 4;
    }

    @Override
    public ChunkTile atCoordinate(int x, int y, int z) {
        switch (y) {
            case 0:
                return ChunkTile.create(x, y, z, Substance.BEDROCK);
            case 1:
            case 2:
                return ChunkTile.create(x, y, z, Substance.DIRT);
            case 3:
                return ChunkTile.create(x, y, z, Substance.GRASS);
            default:
                TridentLogger.error(new IllegalArgumentException("Cannot parse over 4 block height for flats"));
        }

        return null;
    }
}
