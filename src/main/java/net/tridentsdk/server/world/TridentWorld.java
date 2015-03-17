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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import net.tridentsdk.Difficulty;
import net.tridentsdk.GameMode;
import net.tridentsdk.Position;
import net.tridentsdk.Trident;
import net.tridentsdk.base.Block;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.meta.nbt.*;
import net.tridentsdk.server.packets.play.out.PacketPlayOutTimeUpdate;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.threads.ThreadsHandler;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.world.*;
import net.tridentsdk.world.gen.ChunkAxisAlignedBoundingBox;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class TridentWorld implements World {
    private static final int SIZE = 1;
    private static final int MAX_HEIGHT = 255;
    private static final int MAX_CHUNKS = 30_000_000;

    private final ChunkCache loadedChunks = new ChunkCache(this);
    private final Set<Entity> entities = Factories.collect().createSet();
    private final String name;
    private final WorldLoader loader;
    private final Position spawnLocation;

    private volatile long time;
    private volatile long existed;
    private volatile int rainTime;
    private volatile int thunderTime;
    private volatile double borderSize;
    private volatile Dimension dimension;
    private volatile Difficulty difficulty;
    private volatile GameMode defaultGamemode;
    private volatile LevelType type;

    private volatile boolean difficultyLocked;
    private volatile boolean redstoneTick;
    private volatile boolean raining;
    private volatile boolean thundering;

    private TridentWorld(String name, WorldLoader loader, boolean throwaway) {
        this.name = name;
        this.loader = loader;
        this.spawnLocation = Position.create(this, 0, 0, 0);
    }

    TridentWorld(String name, WorldLoader loader) {
        this.name = name;
        this.loader = loader;
        this.spawnLocation = Position.create(this, 0, 0, 0);

        TridentLogger.log("Starting to load " + name + "...");

        File directory = new File(name + File.separator);
        File levelFile = new File(directory, "level.dat");
        CompoundTag level;

        InputStream fis = null;
        try {
            fis = new FileInputStream(levelFile);

            byte[] compressedData = new byte[fis.available()];
            fis.read(compressedData);

            level = new NBTDecoder(new DataInputStream(new ByteArrayInputStream(
                    ByteStreams.toByteArray(new GZIPInputStream(new ByteArrayInputStream(compressedData)))))).decode()
                    .getTagAs("Data");
        } catch (FileNotFoundException ignored) {
            TridentLogger.error(new IllegalArgumentException("Could not find world " + name));
            return;
        } catch (Exception ex) {
            TridentLogger.error("Unable to load level.dat! Printing stacktrace...");
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
        spawnLocation.setX(((IntTag) level.getTag("SpawnX")).value());
        spawnLocation.setY(((IntTag) level.getTag("SpawnY")).value() + 5);
        spawnLocation.setZ(((IntTag) level.getTag("SpawnZ")).value());

        dimension = Dimension.OVERWORLD;
        // difficulty = Difficulty.difficultyOf(((IntTag) level.getTag("Difficulty")).value()); from tests does
        // not exist
        difficulty = Difficulty.NORMAL;
        defaultGamemode = GameMode.gamemodeOf(((IntTag) level.getTag("GameType")).value());
        type = LevelType.levelTypeOf(((StringTag) level.getTag("generatorName")).value());
        borderSize = level.containsTag("BorderSize") ?
                ((DoubleTag) level.getTag("BorderSize")).value() : 6000;

        time = ((LongTag) level.getTag("DayTime")).value();
        existed = ((LongTag) level.getTag("Time")).value();
        raining = ((ByteTag) level.getTag("raining")).value() == 1;
        rainTime = ((IntTag) level.getTag("rainTime")).value();
        thundering = ((ByteTag) level.getTag("thundering")).value() == 1;
        thunderTime = ((IntTag) level.getTag("thunderTime")).value();
        difficultyLocked = level.containsTag("DifficultyLocked") &&
                ((ByteTag) level.getTag("DifficultyLocked")).value() == 1;
        TridentLogger.success("Loaded level.dat successfully. Moving on to region files...");

        // TODO: load other values

        File region = new File(directory, "region" + File.separator);

        if (!(region.exists()) || !(region.isDirectory())) {
            TridentLogger.error(
                    new IllegalStateException("Region folder is rather non-existent or isn't a directory!"));
        }

        TridentLogger.success("Loaded region files successfully. Moving onto player data...");

        TridentLogger.log("Loading spawn chunks...");

        int centX = ((int) Math.floor(spawnLocation.x())) >> 4;
        int centZ = ((int) Math.floor(spawnLocation.z())) >> 4;

        for (int x = centX - 3; x <= centX + 3; x++) {
            for (int z = centZ - 3; z <= centZ + 3; z++) {
                chunkAt(x, z, true);
            }
        }

        TridentLogger.success("Loaded spawn chunks. ");

        File playerData = new File(directory, "playerdata");

        if (!(playerData.exists()) || !(playerData.isDirectory())) {
            TridentLogger.warn("Player data folder does not exist. Creating folder...");
            playerData.mkdir();
        }
    }

    static TridentWorld createWorld(String name, WorldLoader loader) {
        TridentWorld world = null;

        try {
            TridentLogger.log("Starting to create " + name + "...");

            TridentLogger.log("Creating directories and setting values...");
            File directory = new File(name + File.separator);
            File levelFile = new File(directory, "level.dat");
            File region = new File(directory, "region" + File.separator);
            File playerData = new File(directory, "playerdata");
            directory.mkdir();
            levelFile.createNewFile();
            region.mkdir();
            playerData.mkdir();

            world = new TridentWorld(name, loader, false);
            world.dimension = Dimension.OVERWORLD;
            // difficulty = Difficulty.difficultyOf(((IntTag) level.getTag("Difficulty")).value());
            // from tests does not exist
            world.difficulty = Difficulty.NORMAL;
            world.defaultGamemode = GameMode.SURVIVAL;
            world.type = LevelType.DEFAULT;
            world.borderSize = 60000000;
            world.time = 0;
            world.existed = 0;
            world.raining = false;
            world.rainTime = 0;
            world.thundering = false;
            world.thunderTime = 0;
            world.difficultyLocked = false;
            TridentLogger.success("Created directories and set all values");

            // TODO: load other values

            world.spawnLocation.setX(0);
            world.spawnLocation.setY(64);
            world.spawnLocation.setZ(0);

            TridentLogger.log("Loading spawn chunks...");
            int centX = ((int) Math.floor(world.spawnLocation.x())) >> 4;
            int centZ = ((int) Math.floor(world.spawnLocation.z())) >> 4;

            for (ChunkLocation location :
                    new ChunkAxisAlignedBoundingBox(ChunkLocation.create(centX - 7, centZ - 7),
                            ChunkLocation.create(centX + 7, centZ + 7))) {
                TridentChunk chunk = new TridentChunk(world,location);
                world.addChunkAt(location, chunk);
                chunk.generate();
            }

            TridentLogger.success("Loaded spawn chunks.");
        } catch (IOException e) {
            TridentLogger.error(e);
        }

        return world;
    }

    public void tick() {
        ThreadsHandler.worldExecutor().execute(() -> {
            redstoneTick = !redstoneTick;

            if (time >= 2400)
                time = 0;
            if (time % 40 == 0)
                TridentPlayer.sendAll(new PacketPlayOutTimeUpdate().set("worldAge", existed).set("time", time));

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

            if (time % 150 == 0) {
                Set<ChunkLocation> set = Sets.newHashSet();
                for (Entity entity : entities) {
                    if (entity instanceof Player) {
                        Position pos = entity.position();
                        int x = (int) pos.x() % 16;
                        int z = (int) pos.z() % 16;
                        int viewDist = Trident.config().getInt("view-distance", 7);

                        for (int i = x - viewDist; i < x + viewDist; i++) {
                            for (int j = z - viewDist; j < z + viewDist; j++) {
                                set.add(ChunkLocation.create(i, j));
                            }
                        }
                    }
                }

                loadedChunks.retain(set);
                set = null;
            }
        });
    }

    protected void addChunkAt(ChunkLocation location, Chunk chunk) {
        if (location == null) {
            TridentLogger.error(new NullPointerException("Location cannot be null"));
        }

        this.loadedChunks.put(location, (TridentChunk) chunk);
    }

    public Collection<TridentChunk> loadedChunks() {
        return loadedChunks.values();
    }

    public void save() {
        CompoundTag tag = new CompoundTag("Data");

        TridentLogger.log("Saving " + name + "...");
        TridentLogger.log("Attempting to save level data...");

        tag.addTag(new IntTag("SpawnX").setValue((int) spawnLocation.x()));
        tag.addTag(new IntTag("SpawnY").setValue((int) spawnLocation.y()));
        tag.addTag(new IntTag("SpawnZ").setValue((int) spawnLocation.z()));
        tag.addTag(new DoubleTag("BorderSize").setValue(borderSize));

        tag.addTag(new ByteTag("Difficulty").setValue(difficulty.asByte()));
        tag.addTag(new ByteTag("DifficultyLocked").setValue(difficultyLocked ? (byte) 1 : (byte) 0));
        tag.addTag(new LongTag("DayTime").setValue(time));
        tag.addTag(new LongTag("Time").setValue(existed));
        tag.addTag(new ByteTag("raining").setValue(raining ? (byte) 1 : (byte) 0));
        tag.addTag(new IntTag("GameType").setValue(defaultGamemode.asByte()));
        tag.addTag(new StringTag("generatorName").setValue(type.toString()));

        tag.addTag(new IntTag("rainTime").setValue(rainTime));
        tag.addTag(new ByteTag("thundering").setValue(thundering ? (byte) 1 : (byte) 0));
        tag.addTag(new IntTag("thunderTime").setValue(thunderTime));

        // TODO add other level data

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            GZIPOutputStream gzip = new GZIPOutputStream(os);
            CompoundTag root = new CompoundTag("root");

            root.addTag(tag);

            new NBTEncoder(new DataOutputStream(gzip)).encode(root);
            gzip.close();

            Files.write(Paths.get(name, File.separator, "level.dat"), os.toByteArray());
        } catch (IOException | NBTException ex) {
            TridentLogger.warn("Failed to save level data... printing stacktrace");
            TridentLogger.error(ex);
        }

        TridentLogger.log("Saved " + name + " successfully!");


        for (TridentChunk chunk : loadedChunks()) {
            try {
                RegionFile.fromPath(name, chunk.location()).saveChunkData(chunk);
            } catch (IOException | NBTException ex) {
                TridentLogger.warn("Failed to save chunk at (" + chunk.x() +
                        "," + chunk.z() + "), printing stacktrace...");
                TridentLogger.error(ex);
            }
        }
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

        return this.loadedChunks.get(location, generateIfNotFound);
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

        int x = location.x();
        int z = location.z();

        if (x > MAX_CHUNKS || x < -MAX_CHUNKS) {
            return null;
        }

        if (z > MAX_CHUNKS || z < -MAX_CHUNKS) {
            return null;
        }

        TridentChunk tChunk = this.chunkAt(location, false);

        if (tChunk == null) {
            if (this.loader.chunkExists(this, x, z)) {
                Chunk c = this.loader.loadChunk(this, x, z);
                if (c != null) {
                    this.addChunkAt(location, c);
                    return (TridentChunk) c;
                }
            }

            TridentChunk chunk = new TridentChunk(this, x, z);
            this.addChunkAt(location, chunk);
            chunk.generate();
            // DEBUG =====
            TridentLogger.log("Generated chunk at (" + x + "," + z + ")");
            // =====
            
            return chunk;
        }

        return tChunk;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TridentWorld) {
            if (((TridentWorld) obj).name().equals(this.name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Block blockAt(Position location) {
        if (!location.world().name().equals(this.name()))
            throw new IllegalArgumentException("Provided location does not have the same world!");

        int x = (int) Math.round(location.x());
        int y = (int) Math.round(location.y());
        int z = (int) Math.round(location.z());

        return this.chunkAt(WorldUtils.chunkLocation(x, z), true).blockAt(x % 16, y, z % 16);
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
    public WorldLoader loader() {
        return loader;
    }

    @Override
    public LevelType levelType() {
        return type;
    }

    @Override
    public Position spawnLocation() {
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
    public double borderSize() {
        return borderSize;
    }

    @Override
    public Position borderCenter() {
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
        return ImmutableSet.copyOf(this.entities);
    }

    public void addEntity(Entity entity) {
        this.entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        this.entities.remove(entity);
    }

    private static class PlayerFilter implements FilenameFilter {
        @Override
        public boolean accept(File file, String name) {
            return name.endsWith(".dat") && (name.length() == 40); // 40 for UUID, dashes, and extension
        }
    }
}