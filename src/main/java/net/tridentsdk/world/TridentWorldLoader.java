package net.tridentsdk.world;

import net.tridentsdk.api.world.Chunk;
import net.tridentsdk.api.world.World;
import net.tridentsdk.api.world.WorldLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public abstract class TridentWorldLoader implements WorldLoader {

    private List<AtomicReference<World>> worldReferences = new ArrayList<>();

    @Override
    public World load(String world) {
        return new TridentWorld(world, this);
    }

    @Override
    public void save(World world) {
        // TODO
    }

    @Override
    public boolean worldExists(String world) {
        for(AtomicReference<World> reference : worldReferences) {
            if(reference.get().getName().equalsIgnoreCase(world)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean chunkExists(World world, int x, int z) {
        for(Chunk chunk : ((TridentWorld) world).chunks) {
            if(chunk.getX() == x && chunk.getZ() == z) {
                return true;
            }
        }

        return false;
    }
}
