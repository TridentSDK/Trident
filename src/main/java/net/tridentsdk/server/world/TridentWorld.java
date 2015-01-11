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
import io.netty.util.internal.chmv8.ConcurrentHashMapV8;
import net.tridentsdk.Coordinates;
import net.tridentsdk.Difficulty;
import net.tridentsdk.GameMode;
import net.tridentsdk.base.Tile;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.meta.nbt.*;
import net.tridentsdk.server.packets.play.out.PacketPlayOutTimeUpdate;
import net.tridentsdk.server.player.OfflinePlayer;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.threads.ThreadsHandler;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.world.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class TridentWorld implements World {
    private static final int SIZE = 1;
    private static final int MAX_HEIGHT = 255;
    private static final int MAX_CHUNKS = 49; // TODO changed temp for packet compatibility

    private final Map<ChunkLocation, TridentChunk> loadedChunks = new ConcurrentHashMapV8<>();
    private final Set<Entity> entities = Factories.collect().createSet();

    private volatile long age;
    private volatile long time;
    private volatile long existed;
    private volatile int rainTime;
    private volatile int thunderTime;
    private volatile int borderSize;

    private final String name;
    private final Random random;
    private final WorldLoader loader;
    private final Coordinates spawnLocation;
    private volatile Dimension dimension;
    private volatile Difficulty difficulty;
    private volatile GameMode defaultGamemode;
    private volatile LevelType type;

    private volatile boolean difficultyLocked;
    private volatile boolean redstoneTick;
    private volatile boolean raining;
    private volatile boolean thundering;

    TridentWorld(String name, WorldLoader loader) {
        this.name = name;
        this.loader = loader;
        this.random = new Random();

        spawnLocation = Coordinates.create(this, 0d, 0d, 0d);

        TridentLogger.log("Starting to load " + name + "...");
        TridentLogger.log("Attempting to load level.dat...");

        File directory = new File(name + File.separator);
        File levelFile = new File(directory, "level.dat");
        CompoundTag level;

        InputStream fis = null;
        try {
            fis = new FileInputStream(levelFile);

            byte[] compressedData = new byte[fis.available()];
            fis.read(compressedData);

            level = new NBTDecoder(new DataInputStream(new ByteArrayInputStream(ByteStreams.
                    toByteArray(new GZIPInputStream(new ByteArrayInputStream(compressedData)))))).decode()
                    .getTagAs("Data");
        } catch (FileNotFoundException ignored) {
            TridentLogger.error(new IllegalArgumentException("Could not find world " + name));
            return;
        } catch (Exception ex) {
            TridentLogger.log("Unable to load level.dat! Printing stacktrace...");
            TridentLogger.error(ex);
            return;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        TridentLogger.log("Loading values of level.dat....");

        spawnLocation.setX(((IntTag) level.getTag("SpawnX")).getValue());
        spawnLocation.setY(((IntTag) level.getTag("SpawnY")).getValue() + 4);
        spawnLocation.setZ(((IntTag) level.getTag("SpawnZ")).getValue());

        dimension = Dimension.OVERWORLD;
        // difficulty = Difficulty.difficultyOf(((IntTag) level.getTag("Difficulty")).getValue()); from tests does
        // not exist
        difficulty = Difficulty.NORMAL;
        defaultGamemode = GameMode.gamemodeOf(((IntTag) level.getTag("GameType")).getValue());
        type = LevelType.levelTypeOf(((StringTag) level.getTag("generatorName")).getValue());
        borderSize = ((IntTag) level.getTag("BorderSize")).getValue();

        time = ((LongTag) level.getTag("DayTime")).getValue();
        existed = ((LongTag) level.getTag("Time")).getValue();
        raining = ((ByteTag) level.getTag("raining")).getValue() == 1;
        rainTime = ((IntTag) level.getTag("rainTime")).getValue();
        thundering = ((ByteTag) level.getTag("thundering")).getValue() == 1;
        thunderTime = ((IntTag) level.getTag("thunderTime")).getValue();
        difficultyLocked = ((ByteTag) level.getTag("DifficultyLocked")).getValue() == 1;

        TridentLogger.log("Loaded level.dat successfully! Moving on to region files...");

        // TODO: load other values

        File region = new File(directory, "region" + File.separator);

        if (!(region.exists()) || !(region.isDirectory())) {
            TridentLogger.error(
                    new IllegalStateException("Region folder is rather non-existent or isn't a directory!"));
        }

        TridentLogger.log("Loaded region files successfully! Moving onto player data...");

        File playerData = new File(directory, "playerdata");

        if (!(playerData.exists()) || !(playerData.isDirectory())) {
            TridentLogger.log("Player data folder does not exist! Creating folder...");
            playerData.mkdir();
        } else {
            TridentLogger.log("Scanning player data...");

            for (File f : playerData.listFiles(new PlayerFilter())) {
                CompoundTag opData;

                InputStream input = null;
                try {
                    input = new FileInputStream(levelFile);

                    byte[] compressedData = new byte[input.available()];
                    input.read(compressedData);

                    opData = new NBTDecoder(new DataInputStream(new ByteArrayInputStream(ByteStreams.
                            toByteArray(new GZIPInputStream(new ByteArrayInputStream(compressedData)))))).decode();
                } catch (IOException | NBTException ex) {
                    TridentLogger.log("Unable to load " + f.getName() + "! Printing stacktrace...");
                    TridentLogger.error(ex);
                    continue;
                } finally {
                    try {
                        if (input != null) {
                            input.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                new OfflinePlayer(opData, this); // will automatically register itself
            }

            TridentLogger.log("Loaded all player data!");
        }
    }

    public void tick() {
        ThreadsHandler.worldExecutor().execute(new Runnable() {
            @Override
            public void run() {
                redstoneTick = !redstoneTick;

                if (time >= 2400)
                    time = 0;
                if (time % 40 == 0)
                    TridentPlayer.sendAll(new PacketPlayOutTimeUpdate().set("worldAge", age).set("time", time));

                rainTime--;
                thunderTime--;

                if (rainTime <= 0) {
                    raining = !raining;
                    rainTime = ThreadLocalRandom.current().nextInt();
                }

                if (thunderTime <= 0) {
                    thundering = !thundering;
                    thunderTime = ThreadLocalRandom.current().nextInt();
                }

                time++;
                existed++;
            }
        });
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Chunk chunkAt(int x, int z, boolean generateIfNotFound) {
        return this.chunkAt(ChunkLocation.create(x, z), generateIfNotFound);
    }

    @Override
    public TridentChunk chunkAt(ChunkLocation location, boolean generateIfNotFound) {
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
        return this.generateChunk(ChunkLocation.create(x, z));
    }

    @Override
    public TridentChunk generateChunk(ChunkLocation location) {
        if (location == null) {
            TridentLogger.error(new NullPointerException("Location cannot be null"));
            return null;
        }

        int x = location.getX();
        int z = location.getZ();

        if (x > MAX_CHUNKS || x < -MAX_CHUNKS) {
            return null;
        }

        if (z > MAX_CHUNKS || z < -MAX_CHUNKS) {
            return null;
        }

        if (this.chunkAt(location, false) == null) {
            if (this.loader.chunkExists(this, x, z)) {
                this.addChunkAt(location, this.loader.loadChunk(this, x, z));
            } else {
                TridentChunk chunk = new TridentChunk(this, x, z);
                this.addChunkAt(location, chunk);
                chunk.generate();
            }
        }

        return this.chunkAt(location, false);
    }

    public void save() {
        CompoundTag tag = new CompoundTag("Data");

        TridentLogger.log("Saving " + name + "...");
        TridentLogger.log("Attempting to save level data...");

        tag.addTag(new IntTag("SpawnX").setValue((int) spawnLocation.getX()));
        tag.addTag(new IntTag("SpawnY").setValue((int) spawnLocation.getY()));
        tag.addTag(new IntTag("SpawnZ").setValue((int) spawnLocation.getZ()));

        tag.addTag(new ByteTag("Difficulty").setValue(difficulty.asByte()));
        tag.addTag(new ByteTag("DifficultyLocked")
                .setValue(difficultyLocked ? (byte) 1 : (byte) 0));
        tag.addTag(new LongTag("DayTime").setValue(time));
        tag.addTag(new LongTag("Time").setValue(existed));
        tag.addTag(new ByteTag("raining")
                .setValue(raining ? (byte) 1 : (byte) 0));

        tag.addTag(new IntTag("rainTime").setValue(rainTime));
        tag.addTag(new ByteTag("thundering")
                .setValue(thundering ? (byte) 1 : (byte) 0));
        tag.addTag(new IntTag("thunderTime").setValue(thunderTime));

        // TODO add other level data

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            new NBTEncoder(new DataOutputStream(new GZIPOutputStream(os))).encode(tag);
            Files.write(Paths.get(name, File.separator, "level.dat"), os.toByteArray());
        } catch (IOException | NBTException ex) {
            TridentLogger.log("Failed to save level data... printing stacktrace");
            TridentLogger.error(ex);
        }

        // TODO save chunks
    }

    @Override
    public Tile tileAt(Coordinates location) {
        if (!location.world().name().equals(this.name()))
            throw new IllegalArgumentException("Provided location does not have the same world!");

        int x = (int) Math.round(location.getX());
        int y = (int) Math.round(location.getY());
        int z = (int) Math.round(location.getZ());

        return this.chunkAt(WorldUtils.getChunkLocation(x, z), true).tileAt(x % 16, y, z % 16);
    }

    public void addChunkAt(ChunkLocation location, Chunk chunk) {
        if (location == null) {
            TridentLogger.error(new NullPointerException("Location cannot be null"));
        }

        this.loadedChunks.put(location, (TridentChunk) chunk);
    }

    @Override
    public Difficulty difficulty() {
        return difficulty;
    }

    @Override
    public GameMode defaultGamemode() {
        return defaultGamemode;
    }

    @Override
    public LevelType levelType() {
        return type;
    }

    @Override
    public Coordinates spawnLocation() {
        return spawnLocation;
    }

    @Override
    public Dimension dimension() {
        return dimension;
    }

    @Override
    public boolean gameRule(String rule) {
        return false;
    }

    @Override
    public long time() {
        return time;
    }

    @Override
    public boolean isRaining() {
        return raining;
    }

    @Override
    public int rainTime() {
        return rainTime;
    }

    @Override
    public boolean isThundering() {
        return thundering;
    }

    @Override
    public int thunderTime() {
        return thunderTime;
    }

    @Override
    public boolean canGenerateStructures() {
        return false;
    }

    @Override
    public int borderSize() {
        return borderSize;
    }

    @Override
    public Coordinates borderCenter() {
        return null;
    }

    @Override
    public int borderSizeContraction() {
        return 0;
    }

    @Override
    public int borderSizeContractionTime() {
        return 0;
    }

    @Override
    public Set<Entity> entities() {
        return entities;
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


