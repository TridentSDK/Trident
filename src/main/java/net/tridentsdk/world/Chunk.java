package net.tridentsdk.world;

import java.io.Serializable;
import java.util.Random;

public class Chunk implements Serializable {

    private int x, z;
    public World world;

    public Chunk(World world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public void generate() {
        int chunkX = x * 16;
        int chunkZ = z * 16;

        Random r = world.random;

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

    public World getWorld() {
        return world;
    }
}
