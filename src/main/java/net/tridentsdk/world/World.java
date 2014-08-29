package net.tridentsdk.world;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class World implements Serializable {
    public static final int size = 1;
    public static final int maxHeight  = 255;
    public static final int maxChunks = -1;

    private String name;
    public ArrayList<Chunk> chunks = new ArrayList<>();
    public Random random;
    public WorldLoader loader;

    public double spawnX, spawnY, spawnZ;

    public World(String name, WorldLoader loader) {
        this.name = name;
        this.loader = loader;
        this.random = new Random();

        //TODO Set spawn point
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
        if (maxChunks != -1) {
            if (x > maxChunks || x < -maxChunks) {
                return;
            }

            if (z > maxChunks || z < -maxChunks) {
                return;
            }
        }

        if (getChunkAt(x, z, false) == null) {
            if (loader.chunkExists(this, x,z)) {
                chunks.add(loader.loadChunk(this, x, z));

                Chunk c = new Chunk(this, x, z);
                chunks.add(c);
                c.generate();
            }
        }
    }
}
