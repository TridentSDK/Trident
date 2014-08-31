package net.tridentsdk.world;

import net.tridentsdk.api.Block;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.world.Chunk;
import net.tridentsdk.api.world.World;
import net.tridentsdk.api.world.WorldLoader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class TridentWorld implements Serializable, World {
    private static final int SIZE = 1;
    private static final int MAX_HEIGHT = 255;
    private static final int MAX_CHUNKS = -1;

    private String name;
    ArrayList<Chunk> chunks = new ArrayList<>();
    private Random random;
    private WorldLoader loader;

    public Location spawnLocation;

    TridentWorld(String name, WorldLoader loader) {
        this.name = name;
        this.loader = loader;
        this.random = new Random();

        // TODO Set spawn point
    }

    public String getName() {
        return name;
    }

    public Chunk getChunkAt(int x, int z, boolean generateIfNotFound) {
        if (chunks == null) {
            return null;
        }

        for (Chunk chunk : chunks.toArray(new Chunk[chunks.size()])) {
            if (chunk == null) {
                continue;
            }

            if (chunk.getX() == x && chunk.getZ() == z) {
                return chunk;
            }
        }

        if (generateIfNotFound) {
            generateChunk(x, z);
            return getChunkAt(x, z, false);
        } else {
            return null;
        }
    }

    public void generateChunk(int x, int z) {
        if (x > MAX_CHUNKS || x < -MAX_CHUNKS) {
            return;
        }

        if (z > MAX_CHUNKS || z < -MAX_CHUNKS) {
            return;
        }

        if (getChunkAt(x, z, false) == null) {
            if (loader.chunkExists(this, x,z)) {
                chunks.add(loader.loadChunk(this, x, z));

                TridentChunk c = new TridentChunk(this, x, z);
                chunks.add(c);
                c.generate();
            }
        }
    }

    @Override
    public Block getBlockAt(Location location) {
        if(location.getWorld().getName().equals(getName()))
            throw new IllegalArgumentException("Provided location does not have the same world!");

        return null;
    }

}
