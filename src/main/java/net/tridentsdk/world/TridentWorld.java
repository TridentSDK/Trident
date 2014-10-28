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

import net.tridentsdk.api.*;
import net.tridentsdk.api.nbt.*;
import net.tridentsdk.api.util.TridentLogger;
import net.tridentsdk.api.world.Chunk;
import net.tridentsdk.api.world.ChunkLocation;
import net.tridentsdk.api.world.ChunkSnapshot;
import net.tridentsdk.api.world.Dimension;
import net.tridentsdk.api.world.LevelType;
import net.tridentsdk.api.world.World;
import net.tridentsdk.api.world.WorldLoader;

import java.io.*;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.DataFormatException;

public class TridentWorld implements World {
    private static final int SIZE = 1;
    private static final int MAX_HEIGHT = 255;
    private static final int MAX_CHUNKS = 49; // TODO changed temp for packet compatibility

    private final Map<ChunkLocation, TridentChunk> loadedChunks = new ConcurrentHashMap<>();
    private final String name;
    private final Random random;
    private final WorldLoader loader;
    private Dimension dimension;
    private Difficulty difficulty;
    private GameMode defaultGamemode;
    private LevelType type;
    private Location spawnLocation;

    TridentWorld(String name, WorldLoader loader) {
        this.name = name;
        this.loader = loader;
        this.random = new Random();

        spawnLocation = new Location(this, 0d, 0d, 0d);
        TridentLogger logger = Trident.getLogger();

        logger.info("Starting to load " + name + "...");
        logger.info("Attempting to load level.dat...");

        File directory = new File(name + File.separator);
        File levelFile = new File(directory, "level.dat");
        CompoundTag level;

        try {
            level = new NBTDecoder(new DataInputStream(new FileInputStream(levelFile))).decode().getTagAs("Data");
        } catch (FileNotFoundException ignored) {
            return;
        } catch (NBTException ex) {
            logger.info("Unable to load level.dat! Printing stacktrace...");
            ex.printStackTrace();
            return;
        }

        logger.info("Loading values of level.dat....");

        spawnLocation.setX(((IntTag) level.getTag("SpawnX")).getValue());
        spawnLocation.setY(((IntTag) level.getTag("SpawnY")).getValue());
        spawnLocation.setZ(((IntTag) level.getTag("SpawnZ")).getValue());

        dimension = Dimension.OVERWORLD;
        difficulty = Difficulty.getDifficulty(((IntTag) level.getTag("Difficulty")).getValue());
        defaultGamemode = GameMode.getGameMode(((IntTag) level.getTag("GameType")).getValue());
        type = LevelType.getLevelType(((StringTag) level.getTag("generatorName")).getValue());

        logger.info("Loaded level.dat successfully! Moving on to region files...");

        // TODO: load other values

        File region = new File(directory, "region" + File.separator);

        if(!(region.exists()) || !(region.isDirectory())) {
            throw new IllegalStateException("Region folder is rather non-existent or isn't a directory!");
        }

        for(File file : region.listFiles()) {
            String[] strings = file.getName().split("\\.");

            logger.info("Found " + file.getName() + ", checking if valid region file... Will skip if invalid");

            if(strings.length != 3 && !(strings[0].equals("r")) && !(file.getName().endsWith(".mca"))) {
                continue; // not valid region file
            }

            int chunkX;
            int chunkZ;

            try {
                chunkX = (int) Math.floor(Integer.parseInt(strings[1]) * 32);
                chunkZ = (int) Math.floor(Integer.parseInt(strings[2]) * 32);
            } catch (NumberFormatException ex) {
                continue; // not valid
            }

            for(ChunkLocation loc : loadedChunks.keySet()) {
                if(loc.getX() == chunkX && loc.getZ() == chunkZ)
                    continue; // already loaded chunk
            }

            logger.info("Great! " + file.getName() + " is a valid region file, loading contents...");

            RegionFile regionFile;

            try {
                regionFile = new RegionFile(file.toPath());
            } catch (IOException ex) {
                logger.info("Unable to load the region file! Printing stacktrace...");
                ex.printStackTrace();
                continue;
            }

            ChunkLocation location = new ChunkLocation(chunkX, chunkZ);
            TridentChunk chunk;

            try {
                chunk = regionFile.loadChunkData(this);
            } catch (NBTException | IOException | DataFormatException e) {
                logger.info("Unable to load the region file! Printing stacktrace...");
                e.printStackTrace();
                continue;
            }

            loadedChunks.put(location, chunk);
            logger.info("Loaded " + file.getName() + " successfully!");
        }
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
    public TridentChunk getChunkAt(ChunkLocation location, boolean generateIfNotFound) {
        if (location == null) {
            return null;
        }

        TridentChunk chunk = this.loadedChunks.get(location);

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
    public TridentChunk generateChunk(ChunkLocation location) {
        if (location == null)
            throw new NullPointerException("Location cannot be null");

        int x = location.getX();
        int z = location.getZ();

        if (x > MAX_CHUNKS || x < -MAX_CHUNKS) {
            return null;
        }

        if (z > MAX_CHUNKS || z < -MAX_CHUNKS) {
            return null;
        }

        if (this.getChunkAt(location, false) == null) {
            if (this.loader.chunkExists(this, x, z)) {
                this.addChunkAt(location, this.loader.loadChunk(this, x, z));
            } else {
                TridentChunk chunk = new TridentChunk(this, x, z);
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

        this.loadedChunks.put(location, (TridentChunk) chunk);
    }

    @Override
    public Block getBlockAt(Location location) {
        if (!location.getWorld().getName().equals(this.getName()))
            throw new IllegalArgumentException("Provided location does not have the same world!");

        int x = (int) Math.round(location.getX());
        int y = (int) Math.round(location.getY());
        int z = (int) Math.round(location.getZ());

        return this.getChunkAt(WorldUtils.getChunkLocation(x, z),true).getBlockAt(x % 16, y, z % 16);
    }

    @Override
    public ChunkSnapshot getChunkSnapshot() {
        return new ChunkSnapshot(new ConcurrentHashMap<ChunkLocation, Chunk>(loadedChunks));
    }

    @Override
    public Dimension getDimesion() {
        return dimension;
    }

    @Override
    public Difficulty getDifficulty() {
        return difficulty;
    }

    @Override
    public GameMode getDefaultGamemode() {
        return defaultGamemode;
    }

    @Override
    public LevelType getLevelType() {
        return type;
    }

    @Override
    public Location getSpawnLocation() {
        return spawnLocation;
    }
}


