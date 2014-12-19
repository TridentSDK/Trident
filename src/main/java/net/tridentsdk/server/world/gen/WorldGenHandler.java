package net.tridentsdk.server.world.gen;

import net.tridentsdk.world.World;

/**
 * Handles world generation
 *
 * @author The TridentSDK Team
 */
public class WorldGenHandler {
    private final AbstractGenerator generator;

    private WorldGenHandler(AbstractGenerator generator) {
        this.generator = generator;
    }

    public static WorldGenHandler create(AbstractGenerator generator) {
        return new WorldGenHandler(generator);
    }

    public void apply(World world) {
        for (ChunkTile tile : generator.doGen(world))
            tile.apply(world);
    }
}
