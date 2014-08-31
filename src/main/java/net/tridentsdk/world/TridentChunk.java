package net.tridentsdk.world;

import net.tridentsdk.api.world.Chunk;

import java.io.Serializable;
import java.util.Random;

public class TridentChunk implements Serializable, Chunk {

    private int x;
    private int z;
    public TridentWorld world;

    public TridentChunk(TridentWorld world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public void generate() {
        int chunkX = x * 16;
        int chunkZ = z * 16;

        Random r = new Random();

       for (int x = 0; x < 16; x++) {
           for (int z = 0; z < 16; z++) {
               //TODO y

               int y = 0;

               //TODO Place blocks
           }
       }
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public TridentWorld getWorld() {
        return world;
    }
}
