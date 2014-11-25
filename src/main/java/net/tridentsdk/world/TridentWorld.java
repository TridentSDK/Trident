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

import com.google.common.io.ByteStreams;
import net.tridentsdk.api.Block;
import net.tridentsdk.api.Difficulty;
import net.tridentsdk.api.GameMode;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.nbt.*;
import net.tridentsdk.api.util.StringUtil;
import net.tridentsdk.api.world.*;
import net.tridentsdk.player.OfflinePlayer;
import net.tridentsdk.server.TridentServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.DataFormatException;
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
    private Location spawnLocation;

    TridentWorld(String name, WorldLoader loader) {
        this.name = name;
        this.loader = loader;
        this.random = new Random();

        spawnLocation = new Location(this, 0d, 0d, 0d);
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

        return this.getChunkAt(WorldUtils.getChunkLocation(x, z), true).getBlockAt(x % 16, y, z % 16);
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
    public Location getSpawnLocation() {
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
    public Location getSpawn() {
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
    public Location getBorderCenter() {
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


