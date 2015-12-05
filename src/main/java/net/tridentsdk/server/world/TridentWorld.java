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

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import net.tridentsdk.base.Block;
import net.tridentsdk.base.BoundingBox;
import net.tridentsdk.base.Position;
import net.tridentsdk.base.Substance;
import net.tridentsdk.effect.particle.ParticleEffect;
import net.tridentsdk.effect.particle.ParticleEffectType;
import net.tridentsdk.effect.sound.SoundEffect;
import net.tridentsdk.effect.sound.SoundEffectType;
import net.tridentsdk.effect.visual.VisualEffect;
import net.tridentsdk.effect.visual.VisualEffectType;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.Projectile;
import net.tridentsdk.entity.block.SlotProperties;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.living.ProjectileLauncher;
import net.tridentsdk.entity.traits.EntityProperties;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.entity.types.HorseType;
import net.tridentsdk.entity.types.VillagerCareer;
import net.tridentsdk.entity.types.VillagerProfession;
import net.tridentsdk.event.weather.RainEvent;
import net.tridentsdk.event.weather.SunEvent;
import net.tridentsdk.event.weather.ThunderEvent;
import net.tridentsdk.inventory.Item;
import net.tridentsdk.meta.block.Tile;
import net.tridentsdk.meta.nbt.*;
import net.tridentsdk.server.chunk.ChunkHandler;
import net.tridentsdk.server.concurrent.ThreadsHandler;
import net.tridentsdk.server.concurrent.TickSync;
import net.tridentsdk.server.effect.particle.TridentParticleEffect;
import net.tridentsdk.server.effect.sound.TridentSoundEffect;
import net.tridentsdk.server.effect.visual.TridentVisualEffect;
import net.tridentsdk.server.entity.TridentDroppedItem;
import net.tridentsdk.server.entity.TridentEntity;
import net.tridentsdk.server.entity.TridentExpOrb;
import net.tridentsdk.server.entity.TridentFirework;
import net.tridentsdk.server.entity.block.*;
import net.tridentsdk.server.entity.living.*;
import net.tridentsdk.server.entity.projectile.*;
import net.tridentsdk.server.entity.vehicle.*;
import net.tridentsdk.server.event.EventProcessor;
import net.tridentsdk.server.packets.play.out.PacketPlayOutTimeUpdate;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.util.Pair;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.world.*;
import net.tridentsdk.world.gen.ChunkAxisAlignedBoundingBox;
import net.tridentsdk.world.gen.GeneratorRandom;
import net.tridentsdk.world.settings.*;

import javax.annotation.concurrent.ThreadSafe;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Represents a world on the server
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class TridentWorld implements World {
    private static final int SIZE = 1;
    private static final int MAX_HEIGHT = 255;
    private static final int MAX_CHUNKS = 3_750_000; // 60 million blocks
    private static final int CHUNK_EVICTION_TIME = 20 * 60 * 5;

    private final String name;
    private final WorldLoader loader;
    private final Position spawnPosition;

    private final ChunkHandler chunkHandler = new ChunkHandler(this);
    private final Set<Entity> entities = Sets.newConcurrentHashSet();
    private final Set<Tile> tiles = Sets.newConcurrentHashSet();
    private final Map<GameRule, GameRule.Value> gameRules = Maps.newHashMap();

    private final AtomicLong time = new AtomicLong();
    private final AtomicLong existed = new AtomicLong();
    private final AtomicInteger rainTime = new AtomicInteger();
    private final AtomicInteger thunderTime = new AtomicInteger();

    private volatile double borderSize;
    private volatile long seed; // TODO prevent seeds == 0
    private volatile GeneratorRandom random;
    private volatile Dimension dimension;
    private volatile Difficulty difficulty;
    private volatile GameMode defaultGamemode;
    private volatile LevelType type;
    private volatile boolean difficultyLocked;
    private volatile boolean redstoneTick;
    private volatile boolean raining;
    private volatile boolean thundering;
    private volatile boolean generateStructures = true;

    private final WorldSettings settings;
    private final WorldBorder border = new WorldBorder() {
        private volatile Pair<Integer, Integer> center = Pair.immutable(0, 0);
        private volatile int mod = 60000000;
        private volatile int time = 0;

        @Override
        public int size() {
            return 0; // todo calculate size of border?
        }

        @Override // usually modified by plugins atomically relative to the server
        public void modify(int mod, int time) {
            this.mod = mod;
            if (time != 0) this.time = time;
            apply();
        }

        @Override
        public Pair<Integer, Integer> center() {
            return center;
        }

        @Override
        public void setCenter(int x, int z) {
            center = Pair.immutable(x, z);
            apply();
        }

        @Override
        public int sizeContraction() {
            return mod;
        }

        @Override
        public int contractionTime() {
            return time;
        }

        private void apply() {

        }
    };
    private final WeatherConditions conditions = new WeatherConditions() {
        @Override
        public boolean isRaining() {
            return raining;
        }

        @Override
        public void setRaining(boolean rain) {
            if (rain) {
                if (!raining) {
                    toggleRain(0);
                }
            } else {
                if (raining) {
                    toggleRain(0);
                }
            }
        }

        @Override
        public int rainTime() {
            return rainTime.get();
        }

        @Override
        public void toggleRain(int ticks) {
            rainTime.set(ticks);
        }

        @Override
        public boolean isThundering() {
            return thundering;
        }

        @Override
        public void setThundering(boolean thunder) {
            if (thunder) {
                if (!thundering) {
                    toggleThunder(0);
                }
            } else {
                if (thundering) {
                    toggleThunder(0);
                }
            }
        }

        @Override
        public int thunderTime() {
            return thunderTime.get();
        }

        @Override
        public void toggleThunder(int ticks) {
            thunderTime.set(ticks);
        }

        @Override
        public boolean isSunny() {
            return !raining && !thundering;
        }

        @Override
        public void setSunny() {
            setRaining(false);
            setThundering(false);
        }
    };

    private TridentWorld(String name, WorldLoader loader, boolean throwaway) {
        ((TridentWorldLoader) loader).world = this;
        this.name = name;

        WorldCreateOptions options = loader.options();
        this.seed = options.seed();
        this.random = new GeneratorRandom(seed);
        this.loader = loader;
        this.spawnPosition = Position.create(this, 0, 0, 0);

        this.dimension = options.dimension();
        this.difficulty = options.difficulty();
        this.defaultGamemode = options.defaultGameMode();
        // level
        this.gameRules.clear();
        this.gameRules.putAll(options.gameRules());
        this.generateStructures = options.generateStructures();
        this.settings = TridentWorldSettings.load(this, options);
    }

    TridentWorld(String name, WorldLoader loader) {
        ((TridentWorldLoader) loader).world = this;
        this.name = name;
        this.loader = loader;
        this.spawnPosition = Position.create(this, 0, 0, 0);

        TridentLogger.get().log("Starting to load " + name + "...");

        File directory = new File(name + File.separator);
        File levelFile = new File(directory, "level.dat");

        InputStream fis = null;
        try {
            fis = new FileInputStream(levelFile);

            byte[] compressedData = new byte[fis.available()];
            fis.read(compressedData);

            CompoundTag level = new NBTDecoder(new DataInputStream(new ByteArrayInputStream(
                    ByteStreams.toByteArray(new GZIPInputStream(new ByteArrayInputStream(compressedData)))))).decode()
                    .getTagAs("Data");

            TridentLogger.get().log("Loading values of level.dat....");
            spawnPosition.setX(((IntTag) level.getTag("SpawnX")).value());
            spawnPosition.setY(((IntTag) level.getTag("SpawnY")).value() + 5);
            spawnPosition.setZ(((IntTag) level.getTag("SpawnZ")).value());

            dimension = Dimension.OVERWORLD;
            // difficulty = Difficulty.of(((IntTag) level.getTag("Difficulty")).value()); from tests does
            // not exist
            difficulty = Difficulty.NORMAL;
            defaultGamemode = GameMode.of(((IntTag) level.getTag("GameType")).value());
            type = LevelType.of(((StringTag) level.getTag("generatorName")).value());
            seed = ((LongTag) level.getTag("RandomSeed")).value();
            ((TridentWorldLoader) loader).setGenerator(seed);
            random = new GeneratorRandom(seed);

            borderSize = level.containsTag("BorderSize") ?
                    ((DoubleTag) level.getTag("BorderSize")).value() : 6000;

            time.set(((LongTag) level.getTag("DayTime")).value());
            existed.set(((LongTag) level.getTag("Time")).value());
            raining = ((ByteTag) level.getTag("raining")).value() == 1;
            rainTime.set(((IntTag) level.getTag("rainTime")).value());
            thundering = ((ByteTag) level.getTag("thundering")).value() == 1;
            thunderTime.set(((IntTag) level.getTag("thunderTime")).value());
            difficultyLocked = level.containsTag("DifficultyLocked") &&
                    ((ByteTag) level.getTag("DifficultyLocked")).value() == 1;

            WorldCreateOptions options = loader.options();
            options.dimension(dimension)
                    .difficulty(difficulty)
                    .gameMode(defaultGamemode)
                    .level(type)
                    .generator(null) // todo
                    .structures(generateStructures)
                    .pvp(true) // todo
                    .seed(String.valueOf(seed));

            gameRules.forEach(options::rule);

            TridentLogger.get().success("Loaded level.dat successfully. Moving on to region files...");
        } catch (FileNotFoundException ignored) {
            TridentLogger.get().error(new IllegalArgumentException("Could not find world " + name));
            return;
        } catch (Exception ex) {
            TridentLogger.get().error("Unable to load level.dat! Printing stacktrace...");
            TridentLogger.get().error(ex);
            return;
        } finally {
            settings = TridentWorldSettings.load(this, loader.options());
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // TODO: load other values

        File region = new File(directory, "region" + File.separator);

        if (!(region.exists()) || !(region.isDirectory())) {
            TridentLogger.get().error(
                    new IllegalStateException("Region folder is rather non-existent or isn't a directory!"));
            return;
        }

        TridentLogger.get().success("Loaded region files successfully. Moving onto player data...");

        TridentLogger.get().log("Loading spawn chunks...");

        int centX = ((int) Math.floor(spawnPosition.x())) >> 4;
        int centZ = ((int) Math.floor(spawnPosition.z())) >> 4;

        for (ChunkLocation location :
                new ChunkAxisAlignedBoundingBox(ChunkLocation.create(centX - 3, centZ - 3),
                        ChunkLocation.create(centX + 3, centZ + 3))) {
            chunkAt(location, true);
        }

        TridentLogger.get().success("Loaded spawn chunks. ");

        File playerData = new File(directory, "playerdata");

        if (!(playerData.exists()) || !(playerData.isDirectory())) {
            TridentLogger.get().warn("Player data folder does not exist. Creating folder...");
            playerData.mkdir();
        }
    }

    static TridentWorld createWorld(String name, WorldLoader loader) {
        TridentWorld world = null;

        try {
            TridentLogger.get().log("Starting to create " + name + "...");

            TridentLogger.get().log("Creating directories and setting values...");
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
            // difficulty = Difficulty.of(((IntTag) level.getTag("Difficulty")).value());
            // from tests does not exist
            world.difficulty = Difficulty.NORMAL;
            world.defaultGamemode = GameMode.SURVIVAL;
            world.type = LevelType.DEFAULT;
            world.borderSize = 60000000;
            world.time.set(0);
            world.existed.set(0);
            world.raining = false;
            world.rainTime.set(0);
            world.thundering = false;
            world.thunderTime.set(0);
            world.difficultyLocked = false;
            TridentLogger.get().success("Created directories and set all values");

            // TODO: load other values
            TridentLogger.get().log("Loading spawn chunks...");
            int centX = ((int) Math.floor(world.spawnPosition.x())) >> 4;
            int centZ = ((int) Math.floor(world.spawnPosition.z())) >> 4;

            ((TridentWorldLoader) loader).setGenerator(loader.options().seed());

            for (ChunkLocation location :
                    new ChunkAxisAlignedBoundingBox(ChunkLocation.create(centX - 3, centZ - 3),
                            ChunkLocation.create(centX + 3, centZ + 3))) {
                world.chunkAt(location, true);
            }

            world.spawnPosition.setX(0);
            world.spawnPosition.setZ(0);
            int y = ((TridentChunk) world.spawnPosition.chunk()).maxHeightAt(0, 0);
            world.spawnPosition().setY(y + 3);

            world.save();

            TridentLogger.get().success("Loaded spawn chunks.");
        } catch (IOException e) {
            TridentLogger.get().error(e);
        }

        return world;
    }

    public void tick() {
        ThreadsHandler.worldExecutor().execute(() -> {
            redstoneTick = !redstoneTick;

            long currentTime = time.get();

            rainTime.getAndDecrement();
            thunderTime.getAndDecrement();

            if (rainTime.get() <= 0) {
                raining = !raining;
                if (raining) {
                    RainEvent e = EventProcessor.fire(new RainEvent(this));
                    if (e.isIgnored()) {
                        raining = false;
                    }
                } else {
                    SunEvent event = EventProcessor.fire(new SunEvent(this));
                    if (event.isIgnored()) {
                        raining = true;
                    }
                }

                rainTime.set(ThreadLocalRandom.current().nextInt());
            }

            if (thunderTime.get() <= 0) {
                thundering = !thundering;
                if (thundering) {
                    ThunderEvent e = EventProcessor.fire(new ThunderEvent(this));
                    if (e.isIgnored()) {
                        thundering = false;
                    }
                } else {
                    // TODO do we really want this?
                    SunEvent event = EventProcessor.fire(new SunEvent(this));
                    if (event.isIgnored()) {
                        thundering = true;
                    }
                }

                thunderTime.set(ThreadLocalRandom.current().nextInt());
            }

            boolean updateTime = (currentTime & 40) == 0;

            for (Entity entity : entities) {
                TickSync.increment("ENTITY: uuid-" + entity.uniqueId().toString() + " id-" + entity.entityId() + " type-" + entity.type());
                ((TridentEntity) entity).tick();
                if (entity instanceof Player) {
                    TridentPlayer player = (TridentPlayer) entity;

                    if (updateTime) {
                        player.connection().sendPacket(new PacketPlayOutTimeUpdate().set("worldAge", existed.get()).set("time", currentTime));
                    }
                }
            }

            /* if ((existed.get() & CHUNK_EVICTION_TIME) == 0) {
                UnmodifiableIterator<List<ChunkLocation>> list = Iterators.partition(Sets.newHashSet(chunkHandler.keys()).iterator(),
                        Math.max(TridentPlayer.players().size(), 1));
                for (; list.hasNext(); ) {
                    List<ChunkLocation> chunks = list.next();
                    ThreadsHandler.chunkExecutor().execute(() -> chunks.forEach(chunkHandler::tryRemove));
                }
            } */

            if (currentTime >= 24000)
                time.set(0);
            else time.getAndIncrement();
            existed.getAndIncrement();
            TickSync.complete("WORLD: " + name());
        });
    }

    protected void addChunkAt(ChunkLocation location, Chunk chunk) {
        if (location == null) {
            TridentLogger.get().error(new NullPointerException("Location cannot be null"));
        }

        this.chunkHandler.put((TridentChunk) chunk);
    }

    public GeneratorRandom random() {
        return random;
    }

    public void save() {
        CompoundTag tag = new CompoundTag("Data");

        TridentLogger.get().log("Saving " + name + "...");
        TridentLogger.get().log("Attempting to save level data...");

        tag.addTag(new IntTag("SpawnX").setValue((int) spawnPosition.x()));
        tag.addTag(new IntTag("SpawnY").setValue((int) spawnPosition.y()));
        tag.addTag(new IntTag("SpawnZ").setValue((int) spawnPosition.z()));
        tag.addTag(new DoubleTag("BorderSize").setValue(borderSize));

        tag.addTag(new ByteTag("Difficulty").setValue(difficulty.asByte()));
        tag.addTag(new ByteTag("DifficultyLocked").setValue(difficultyLocked ? (byte) 1 : (byte) 0));
        tag.addTag(new LongTag("DayTime").setValue(time.get()));
        tag.addTag(new LongTag("Time").setValue(existed.get()));
        tag.addTag(new ByteTag("raining").setValue(raining ? (byte) 1 : (byte) 0));
        tag.addTag(new IntTag("GameType").setValue(defaultGamemode.asByte()));
        tag.addTag(new StringTag("generatorName").setValue(type.toString()));
        tag.addTag(new LongTag("RandomSeed").setValue(seed));

        tag.addTag(new IntTag("rainTime").setValue(rainTime.get()));
        tag.addTag(new ByteTag("thundering").setValue(thundering ? (byte) 1 : (byte) 0));
        tag.addTag(new IntTag("thunderTime").setValue(thunderTime.get()));

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
            TridentLogger.get().warn("Failed to save level data... printing stacktrace");
            TridentLogger.get().error(ex);
        }

        for (TridentChunk chunk : loadedChunks()) {
            RegionFile.fromPath(name, chunk.location()).saveChunkData(chunk);
            // System.out.println("saved " + chunk.x() + ":" + chunk.z());
        }

        TridentLogger.get().log("Saved " + name + " successfully!");
    }

    private Entity internalSpawn(Entity entity) {
        ((TridentEntity) entity).spawn();
        return addEntity(entity);
    }

    public Entity addEntity(Entity entity) {
        this.entities.add(entity);
        return entity;
    }

    public void removeEntity(Entity entity) {
        this.entities.remove(entity);

        TridentChunk c = (TridentChunk) entity.position().chunk();
        if (!c.entitiesInternal().remove(entity)) {
            for (Chunk chunk : chunkHandler.values()) {
                // If we don't do this a simple concurrency miss
                // can lead to a memory leak
                if (((TridentChunk) chunk).entitiesInternal().remove(entity)) return;
            }

            throw new IllegalStateException("Entity " + entity.entityId() +
                    " type " + entity.type() +
                    " could not be removed from " + entity.position());
        }
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Collection<Chunk> chunks() {
        return Collections2.transform(chunkHandler.values(), c -> (Chunk) c);
    }

    public Collection<TridentChunk> loadedChunks() {
        return chunkHandler.values();
    }

    public ChunkHandler chunkHandler() {
        return chunkHandler;
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

        return this.chunkHandler.get(location, generateIfNotFound);
    }

    @Override
    public Chunk generateChunk(int x, int z) {
        return this.generateChunk(ChunkLocation.create(x, z));
    }

    @Override
    public TridentChunk generateChunk(ChunkLocation location) {
        if (location == null) {
            TridentLogger.get().error(new NullPointerException("Location cannot be null"));
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
            if (this.loader.chunkExists(x, z)) {
                Chunk c = this.loader.loadChunk(x, z);
                if (c != null) {
                    this.addChunkAt(location, c);
                    return (TridentChunk) c;
                }
            }

            TridentChunk chunk = new TridentChunk(this, x, z);
            this.addChunkAt(location, chunk);
            chunk.generate();
            // DEBUG =====
            //TridentLogger.get().log("Generated chunk at (" + x + "," + z + ")");
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

        int x = (int) Math.floor(location.x());
        int y = (int) Math.floor(location.y());
        int z = (int) Math.floor(location.z());

        return this.chunkAt(WorldUtils.chunkLocation(x, z), true).blockAt(x & 15, y, z & 15);
    }

    @Override
    public WorldLoader loader() {
        return loader;
    }

    @Override
    public Position spawnPosition() {
        return spawnPosition;
    }

    @Override
    public WeatherConditions weather() {
        return conditions;
    }

    @Override
    public WorldSettings settings() {
        return this.settings;
    }

    @Override
    public WorldBorder border() {
        return border;
    }

    @Override
    public long time() {
        return time.get();
    }

    @Override
    public Entity spawn(EntityType type, Position spawnPosition) {
        switch (type) {
            case NOT_IMPL:
                throw new UnsupportedOperationException("Cannot spawn unimplemented entity");
            case CREEPER:
                return internalSpawn(new TridentCreeper(UUID.randomUUID(), spawnPosition));
            case BAT:
                return internalSpawn(new TridentBat(UUID.randomUUID(), spawnPosition));
            case BLAZE:
                return internalSpawn(new TridentBlaze(UUID.randomUUID(), spawnPosition));
            case CHICKEN:
                return internalSpawn(new TridentChicken(UUID.randomUUID(), spawnPosition));
            case COW:
                return internalSpawn(new TridentCow(UUID.randomUUID(), spawnPosition));
            case ENDER_DRAGON:
                return internalSpawn(new TridentEnderDragon(UUID.randomUUID(), spawnPosition));
            case ENDERMAN:
                return internalSpawn(new TridentEnderDragon(UUID.randomUUID(), spawnPosition));
            case ENDERMITE:
                return internalSpawn(new TridentEndermite(UUID.randomUUID(), spawnPosition));
            case GHAST:
                return internalSpawn(new TridentGhast(UUID.randomUUID(), spawnPosition));
            case HORSE:
                return internalSpawn(new TridentHorse(UUID.randomUUID(), spawnPosition, HorseType.HORSE));
            case MAGMA_CUBE:
                return internalSpawn(new TridentMagmaCube(UUID.randomUUID(), spawnPosition));
            case MOOSHROOM:
                return internalSpawn(new TridentMooshroom(UUID.randomUUID(), spawnPosition));
            case OCELOT:
                return internalSpawn(new TridentOcelot(UUID.randomUUID(), spawnPosition));
            case PIG:
                return internalSpawn(new TridentPig(UUID.randomUUID(), spawnPosition));
            case GUARDIAN:
                return internalSpawn(new TridentGuardian(UUID.randomUUID(), spawnPosition));
            case PLAYER:
                // Handle specially
            case RABBIT:
                return internalSpawn(new TridentRabbit(UUID.randomUUID(), spawnPosition));
            case SHEEP:
                return internalSpawn(new TridentSheep(UUID.randomUUID(), spawnPosition));
            case SKELETON:
                return internalSpawn(new TridentSkeleton(UUID.randomUUID(), spawnPosition));
            case SLIME:
                return internalSpawn(new TridentSlime(UUID.randomUUID(), spawnPosition));
            case VILLAGER:
                return internalSpawn(new TridentVillager(UUID.randomUUID(), spawnPosition,
                        VillagerCareer.FARMER, VillagerProfession.FARMER));
            case WITHER:
                return internalSpawn(new TridentWither(UUID.randomUUID(), spawnPosition));
            case WOLF:
                return internalSpawn(new TridentWolf(UUID.randomUUID(), spawnPosition));
            case ZOMBIE:
                return internalSpawn(new TridentZombie(UUID.randomUUID(), spawnPosition));
            case ARMOR_STAND:
                return internalSpawn(new TridentArmorStand(UUID.randomUUID(), spawnPosition, new SlotProperties() {
                }));
            case FALLING_BLOCK:
                return internalSpawn(new TridentFallingBlock(UUID.randomUUID(), spawnPosition));
            case ITEM_FRAME:
                return internalSpawn(new TridentItemFrame(UUID.randomUUID(), spawnPosition));
            case PAINTING:
                return internalSpawn(new TridentPainting(UUID.randomUUID(), spawnPosition));
            case PRIMED_TNT:
                return internalSpawn(new TridentPrimeTNT(UUID.randomUUID(), spawnPosition));
            case ARROW:
                return internalSpawn(new TridentArrow(UUID.randomUUID(), spawnPosition, new ProjectileLauncher() {
                    @Override
                    public <T extends Projectile> T launchProjectile(EntityProperties properties) {
                        return null;
                    }
                }));
            case EGG:
                return internalSpawn(new TridentEgg(UUID.randomUUID(), spawnPosition, new ProjectileLauncher() {
                    @Override
                    public <T extends Projectile> T launchProjectile(EntityProperties properties) {
                        return null;
                    }
                }));
            case ENDER_PEARL:
                return internalSpawn(new TridentEnderPearl(UUID.randomUUID(), spawnPosition, new ProjectileLauncher() {
                    @Override
                    public <T extends Projectile> T launchProjectile(EntityProperties properties) {
                        return null;
                    }
                }));
            case EXPERIENCE_BOTTLE:
                return internalSpawn(new TridentExpBottle(UUID.randomUUID(), spawnPosition, new ProjectileLauncher() {
                    @Override
                    public <T extends Projectile> T launchProjectile(EntityProperties properties) {
                        return null;
                    }
                }));
            case FIREBALL:
                return internalSpawn(new TridentFireball(UUID.randomUUID(), spawnPosition,
                        new ProjectileLauncher() {
                            @Override
                            public <T extends Projectile> T launchProjectile(EntityProperties properties) {
                                return null;
                            }
                        }));
            case FISH_HOOK:
                return internalSpawn(new TridentFishHook(UUID.randomUUID(), spawnPosition, new ProjectileLauncher() {
                    @Override
                    public <T extends Projectile> T launchProjectile(EntityProperties properties) {
                        return null;
                    }
                }));
            case POTION:
                return internalSpawn(new TridentPotion(UUID.randomUUID(), spawnPosition, new ProjectileLauncher() {
                    @Override
                    public <T extends Projectile> T launchProjectile(EntityProperties properties) {
                        return null;
                    }
                }));
            case SMALL_FIREBALL:
                return internalSpawn(new TridentSmallFireball(UUID.randomUUID(), spawnPosition,
                        new ProjectileLauncher() {
                            @Override
                            public <T extends Projectile> T launchProjectile(EntityProperties properties) {
                                return null;
                            }
                        }));
            case SNOWBALL:
                return internalSpawn(new TridentSnowball(UUID.randomUUID(), spawnPosition, new ProjectileLauncher() {
                    @Override
                    public <T extends Projectile> T launchProjectile(EntityProperties properties) {
                        return null;
                    }
                }));
            case WITHER_SKULL:
                return internalSpawn(new TridentWitherSkull(UUID.randomUUID(), spawnPosition, new ProjectileLauncher() {
                    @Override
                    public <T extends Projectile> T launchProjectile(EntityProperties properties) {
                        return null;
                    }
                }));
            case BOAT:
                return internalSpawn(new TridentBoat(UUID.randomUUID(), spawnPosition));
            case COMMAND_MINECART:
                return internalSpawn(new TridentCmdMinecart(UUID.randomUUID(), spawnPosition));
            case FURNANCE_MINECART:
                return internalSpawn(new TridentFurnaceMinecart(UUID.randomUUID(), spawnPosition));
            case HOPPER_MINECART:
                return internalSpawn(new TridentHopperMinecart(UUID.randomUUID(), spawnPosition));
            case MINECART:
                return internalSpawn(new TridentMinecart(UUID.randomUUID(), spawnPosition));
            case SPAWNER_MINECART:
                return internalSpawn(new TridentSpawnerMinecart(UUID.randomUUID(), spawnPosition));
            case TNT_MINECART:
                return internalSpawn(new TridentTntMinecart(UUID.randomUUID(), spawnPosition));
            case ITEM:
                return internalSpawn(new TridentDroppedItem(spawnPosition, new Item(Substance.STONE)));
            case EXPERIENCE_ORB:
                return internalSpawn(new TridentExpOrb(UUID.randomUUID(), spawnPosition));
            case FIREWORK:
                return internalSpawn(new TridentFirework(UUID.randomUUID(), spawnPosition));
        }

        // If it reaches here... that is really bad....
        throw new UnsupportedOperationException("Cannot spawn that type of entity");
    }

    @Override
    public Set<Entity> entities() {
        return ImmutableSet.copyOf(this.entities);
    }

    public Set<Entity> internalEntities() {
        return this.entities;
    }

    public Set<Tile> tilesInternal() {
        return tiles;
    }

    @Override
    public ParticleEffect spawnParticle(ParticleEffectType particle) {
        return new TridentParticleEffect(this, particle);
    }

    @Override
    public VisualEffect spawnVisual(VisualEffectType visual) {
        return new TridentVisualEffect(this, visual);
    }

    @Override
    public SoundEffect playSound(SoundEffectType sound) {
        return new TridentSoundEffect(this, sound);
    }

    public ArrayList<Entity> getEntities(Entity exclude, BoundingBox boundingBox, Predicate<? super Entity> predicate) {
        ArrayList<Entity> list = new ArrayList<>();
        int minX = (int) Math.floor((boundingBox.minX() - 2.0D) / 16.0D);
        int maxX = (int) Math.floor((boundingBox.maxX() + 2.0D) / 16.0D);
        int minZ = (int) Math.floor((boundingBox.minZ() - 2.0D) / 16.0D);
        int maxZ = (int) Math.floor((boundingBox.maxZ() + 2.0D) / 16.0D);
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                Chunk chunk = chunkAt(x, z, false);
                if (chunk != null) {
                    list.addAll(chunk.getEntities(exclude, boundingBox, predicate));
                }
            }
        }
        return list;
    }
}