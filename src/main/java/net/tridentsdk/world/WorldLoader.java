package net.tridentsdk.world;

public abstract class WorldLoader {
    public abstract World load(String world);
    public abstract void save(World world);

    public abstract boolean worldExists(String world);
    public abstract boolean chunkExists(World world, int x, int z);

    public abstract Chunk loadChunk(World world, int x, int z);
    public abstract void saveChunk(Chunk chunk);
}
