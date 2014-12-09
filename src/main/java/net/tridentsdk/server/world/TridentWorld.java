/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tridentsdk.server.world;

import com.google.common.io.ByteStreams;
import net.tridentsdk.Coordinates;
import net.tridentsdk.Difficulty;
import net.tridentsdk.GameMode;
import net.tridentsdk.base.Tile;
import net.tridentsdk.meta.nbt.*;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.player.OfflinePlayer;
import net.tridentsdk.world.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;

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
    private final Coordinates spawnLocation;

    TridentWorld(String name, WorldLoader loader) {
        this.name = name;
        this.loader = loader;
        this.random = new Random();

        spawnLocation = new Coordinates(this, 0d, 0d, 0d);
        Logger logger = LoggerFactory.getLogger(TridentServer.class);

        logger.info("Starting to load " + name + "...");
        logger.info("Attempting to load level.dat...");

        File directory = new File(name + File.separator);
        File levelFile = new File(directory, "level.dat");
        CompoundTag level;

        try {
            InputStream fis = new FileInputStream(levelFile);

            byte[] compressedData = new byte[fis.available()];
            fis.read(compressedData);

            level = new NBTDecoder(new DataInputStream(new ByteArrayInputStream(ByteStreams.
                    toByteArray(new GZIPInputStream(new ByteArrayInputStream(compressedData))))))
                    .decode().getTagAs("Data");
        } catch (FileNotFoundException ignored) {
            return;
        } catch (Exception ex) {
            logger.info("Unable to load level.dat! Printing stacktrace...");
            ex.printStackTrace();
            return;
        }

        logger.info("Loading values of level.dat....");

        spawnLocation.setX(((IntTag) level.getTag("SpawnX")).getValue());
        spawnLocation.setY(((IntTag) level.getTag("SpawnY")).getValue());
        spawnLocation.setZ(((IntTag) level.getTag("SpawnZ")).getValue());

        dimension = Dimension.OVERWORLD;
        // difficulty = Difficulty.getDifficulty(((IntTag) level.getTag("Difficulty")).getValue()); from tests does not exist
        difficulty = Difficulty.NORMAL;
        defaultGamemode = GameMode.getGameMode(((IntTag) level.getTag("GameType")).getValue());
        type = LevelType.getLevelType(((StringTag) level.getTag("generatorName")).getValue());

        logger.info("Loaded level.dat successfully! Moving on to region files...");

        // TODO: load other values

        File region = new File(directory, "region" + File.separator);

        if (!(region.exists()) || !(region.isDirectory())) {
            throw new IllegalStateException("Region folder is rather non-existent or isn't a directory!");
        }

        logger.info("Loaded region files successfully! Moving onto player data...");

        File playerData = new File(directory, "playerdata");

        if (!(playerData.exists()) || !(playerData.isDirectory())) {
            logger.info("Player data folder does not exist! Creating folder...");
            playerData.mkdir();
        } else {
            logger.info("Scanning player data...");

            for (File f : playerData.listFiles(new PlayerFilter())) {
                CompoundTag opData;

                try {
                    InputStream fis = new FileInputStream(levelFile);

                    byte[] compressedData = new byte[fis.available()];
                    fis.read(compressedData);

                    opData = new NBTDecoder(new DataInputStream(new ByteArrayInputStream(ByteStreams.
                            toByteArray(new GZIPInputStream(new ByteArrayInputStream(compressedData)))))).decode();
                } catch (IOException | NBTException ex) {
                    logger.info("Unable to load " + f.getName() + "! Printing stacktrace...");
                    ex.printStackTrace();
                    continue;
                }

                new OfflinePlayer(opData, this); // will automatically register itself
            }

            logger.info("Loaded all player data!");
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

    @Override
    public Tile getTileAt(Coordinates location) {
        return null;
    }

    private void addChunkAt(ChunkLocation location, Chunk chunk) {
        if (location == null) {
            throw new NullPointerException("Location cannot be null");
        }

        this.loadedChunks.put(location, (TridentChunk) chunk);
    }

    @Override
    public ChunkSnapshot getChunkSnapshot() {
        return new ChunkSnapshot(new ConcurrentHashMap<ChunkLocation, Chunk>(loadedChunks));
    }

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
    public Coordinates getSpawnLocation() {
        return spawnLocation;
    }

    @Override
    public Dimension getDimension() {
        return null;
    }

    @Override
    public boolean getGamerule(String rule) {
        return false;
    }

    @Override
    public long getTime() {
        return 0;
    }

    @Override
    public Coordinates getSpawn() {
        return spawnLocation;
    }

    @Override
    public boolean isRaining() {
        return false;
    }

    @Override
    public int getRainTime() {
        return 0;
    }

    @Override
    public boolean isThundering() {
        return false;
    }

    @Override
    public int getThunderTime() {
        return 0;
    }

    @Override
    public boolean canGenerateStructures() {
        return false;
    }

    @Override
    public int getBorderSize() {
        return 0;
    }

    @Override
    public Coordinates getBorderCenter() {
        return null;
    }

    @Override
    public int getBorderSizeContraction() {
        return 0;
    }

    @Override
    public int getBorderSizeContractionTime() {
        return 0;
    }

    private static class PlayerFilter implements FilenameFilter {
        @Override
        public boolean accept(File file, String name) {
            return name.endsWith(".dat") && (name.length() == 41); // 41 for UUID, dashes, and extension
        }
    }

    private static class ChunkFilter implements FilenameFilter {

        @Override
        public boolean accept(File file, String s) {
            String[] strings = s.split("\\.");

            return s.endsWith(".mca") && strings.length == 4 && strings[0].equals("r");
        }
    }
}


