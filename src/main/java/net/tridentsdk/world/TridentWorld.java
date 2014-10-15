/*
 *     Trident - A Multithreaded Server Alternative
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.world;

import net.tridentsdk.api.Block;
import net.tridentsdk.api.Difficulty;
import net.tridentsdk.api.GameMode;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.world.Chunk;
import net.tridentsdk.api.world.ChunkLocation;
import net.tridentsdk.api.world.ChunkSnapshot;
import net.tridentsdk.api.world.Dimension;
import net.tridentsdk.api.world.LevelType;
import net.tridentsdk.api.world.World;
import net.tridentsdk.api.world.WorldLoader;

import java.io.Serializable;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class TridentWorld implements Serializable, World {
    private static final int SIZE = 1;
    private static final int MAX_HEIGHT = 255;
    private static final int MAX_CHUNKS = -1;
    private static final long serialVersionUID = 2892463980167406259L;

    private final Map<ChunkLocation, Chunk> loadedChunks = new ConcurrentHashMap<>();
    private final String name;
    private final Random random;
    private final WorldLoader loader;
    private Location spawnLocation;

    TridentWorld(String name, WorldLoader loader) {
        this.name = name;
        this.loader = loader;
        this.random = new Random();

        // TODO Set spawn point
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Chunk getChunkAt(int x, int z, boolean generateIfNotFound) {
        return this.getChunkAt(new ChunkLocation(x, z), generateIfNotFound);
    }

    @Override
    public Chunk getChunkAt(ChunkLocation location, boolean generateIfNotFound) {
        if (location == null) {
            return null;
        }

        Chunk chunk = this.loadedChunks.get(location);

        if (chunk == null && generateIfNotFound) {
            return this.generateChunk(location);
        } else {
            return chunk;
        }
    }

    @Override
    public Chunk generateChunk(int x, int z) {
        return this.generateChunk(new ChunkLocation(x, z));
    }

    @Override
    public Chunk generateChunk(ChunkLocation location) {
        if (location == null)
            throw new NullPointerException("Location cannot be null");

        int x = location.getX();
        int z = location.getZ();

        if (x > TridentWorld.MAX_CHUNKS || x < -TridentWorld.MAX_CHUNKS) {
            return null;
        }

        if (z > TridentWorld.MAX_CHUNKS || z < -TridentWorld.MAX_CHUNKS) {
            return null;
        }

        if (this.getChunkAt(location, false) == null) {
            if (this.loader.chunkExists(this, x, z)) {
                this.addChunkAt(location, this.loader.loadChunk(this, x, z));
            } else {
                Chunk chunk = new TridentChunk(this, x, z);
                this.addChunkAt(location, chunk);
                chunk.generate();
            }
        }

        return this.getChunkAt(location, false);
    }

    private void addChunkAt(ChunkLocation location, Chunk chunk) {
        if (location == null) {
            throw new NullPointerException("Location cannot be null");
        }

        this.loadedChunks.put(location, chunk);
    }

    @Override
    public Block getBlockAt(Location location) {
        if (!location.getWorld().getName().equals(this.getName()))
            throw new IllegalArgumentException("Provided location does not have the same world!");

        int x = (int) Math.round(location.getX());
        int y = (int) Math.round(location.getY());
        int z = (int) Math.round(location.getZ());

        return this.getChunkAt(WorldUtils.getChunkLocation(x,z),true).getBlockAt(x%16,y,z%16);
    }

    @Override
    public ChunkSnapshot getChunkSnapshot() {
        return new ChunkSnapshot(this.loadedChunks);
    }

    @Override
    public Dimension getDimesion() {
        return Dimension.OVERWORLD; // psuedo return
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.NORMAL; // psuedo return
    }

    @Override
    public GameMode getDefaultGamemode() {
        return GameMode.SURVIVAL; // psuedo return
    }

    @Override
    public LevelType getLevelType() {
        return LevelType.DEFAULT; // psuedo return
    }
}


