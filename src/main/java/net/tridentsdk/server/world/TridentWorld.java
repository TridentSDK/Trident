/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
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

import lombok.Getter;
import net.tridentsdk.base.Block;
import net.tridentsdk.base.Position;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.meta.nbt.Tag;
import net.tridentsdk.server.concurrent.PoolSpec;
import net.tridentsdk.server.concurrent.ServerThreadPool;
import net.tridentsdk.server.entity.TridentEntity;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.world.opt.GenOptImpl;
import net.tridentsdk.server.world.opt.WeatherImpl;
import net.tridentsdk.server.world.opt.WorldBorderImpl;
import net.tridentsdk.server.world.opt.WorldOptImpl;
import net.tridentsdk.world.Chunk;
import net.tridentsdk.world.World;
import net.tridentsdk.world.opt.WorldCreateSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Implementation class for {@link World}.
 */
@ThreadSafe
public class TridentWorld implements World {
    /**
     * The global thread pool used for processing ticking
     * tasks.
     */
    private static final ServerThreadPool TP = ServerThreadPool.forSpec(PoolSpec.WORLDS);
    /**
     * Ticking task used to prevent initialization on each
     * call to the ticking handler
     */
    private final Runnable tickingTask = this::doTick;

    /**
     * The chunk collection
     */
    // again this getState is only ok to init before proper
    // construction because the way we generate worlds is
    // ensuring that the entire world has loaded (read:
    // all chunks) before it is returned in WorldLoader
    private final ChunkMap chunks = new ChunkMap(this);
    /**
     * Name of the world
     */
    @Getter
    private final String name;
    /**
     * The enclosing folder of the world directory
     */
    @Getter
    private final Path directory;
    /**
     * The implementation world options
     */
    @Getter
    private final WorldOptImpl worldOptions;
    /**
     * The implementation generator options
     */
    @Getter
    private final GenOptImpl generatorOptions;
    /**
     * The implementation of the world border
     */
    @Getter
    private final WorldBorderImpl border = new WorldBorderImpl(this);
    /**
     * The implementation of the world's current weather
     */
    @Getter
    private final WeatherImpl weather = new WeatherImpl(this);

    /**
     * The current world time, in ticks.
     *
     * <p>One day/night cycle = 20 min * 60 sec/min * 20 TPS
     * = 24000 ticks total then resets.</p>
     */
    private final AtomicInteger time = new AtomicInteger();

    /**
     * The players that occupy this world
     */
    @Getter
    private final Set<TridentPlayer> occupants = Collections.newSetFromMap(new ConcurrentHashMap<>());
    /**
     * The entities that occupy this world
     */
    @Getter
    private final Set<TridentEntity> entitySet = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * Creates a new world with the given name, folder, and
     * creation options.
     *
     * @param name the name of the new world
     */
    public TridentWorld(String name, Path enclosing, WorldCreateSpec spec) {
        this.name = name;
        this.directory = enclosing;
        // this is only ok because we aren't passing the
        // instance to another thread viewable object
        this.worldOptions = new WorldOptImpl(this, spec);
        this.generatorOptions = new GenOptImpl(spec);
    }

    /**
     * Loads a new world with the given name and folder.
     *
     * @param name the name of the world
     * @param enclosing the enclosing folder
     */
    public TridentWorld(String name, Path enclosing) {
        this.name = name;
        this.directory = enclosing;

        try (GZIPInputStream stream = new GZIPInputStream(new FileInputStream(this.directory.resolve("level.dat").toFile()))) {
            Tag.Compound root = Tag.decode(new DataInputStream(stream));
            Tag.Compound compound = root.getCompound("Data");

            this.worldOptions = new WorldOptImpl(this, compound);
            this.generatorOptions = new GenOptImpl(compound);
            this.weather.read(compound);
            this.border.read(compound);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The world ticking method.
     */
    public final void tick() {
        // performs #doTick
        TP.execute(this.tickingTask);
    }

    // Ticking implementation
    private void doTick() {
        int curTime;
        int newTime;
        do {
            curTime = this.time.get();
            newTime = curTime + 1;
            if (newTime == 24000) {
                newTime = 0;
            }
        } while (!this.time.compareAndSet(curTime, newTime));

        this.chunks.forEach(TridentChunk::tick);
    }

    @Override
    public int getTime() {
        return this.time.get();
    }

    @Override
    public Set<? extends Player> getPlayers() {
        return Collections.unmodifiableSet(this.occupants);
    }

    @Override
    public Stream<? extends Entity> getEntities() {
        return Stream.concat(this.occupants.stream(), this.entitySet.stream());
    }

    @Nonnull
    @Override
    public TridentChunk getChunkAt(int x, int z) {
        return this.chunks.get(x, z, true);
    }

    @Nullable
    @Override
    public TridentChunk getChunkAt(int x, int z, boolean gen) {
        return this.chunks.get(x, z, gen);
    }

    @Override
    public Collection<? extends Chunk> getLoadedChunks() {
        return Collections.unmodifiableCollection(this.chunks.values());
    }

    @Override
    public int getHighestY(int x, int z) {
        return this.getChunkAt(x >> 4, z >> 4).getHighestY(x & 15, z & 15);
    }

    @Override
    public Block getBlockAt(int x, int y, int z) {
        return new TridentBlock(new Position(this, x, y, z));
    }

    /**
     * Removes the chunk from memory, without doing any save
     * or file write operations that are necessary to
     * cleanup.
     *
     * @param x the chunk X coordinate
     * @param z the chunk Z coordinate
     * @return the chunk that was removed, or {@code null}
     * if no chunk was removed
     */
    public TridentChunk removeChunkAt(int x, int z) {
        return this.chunks.remove(x, z);
    }

    @Override
    public Block getBlockAt(Position pos) {
        return new TridentBlock(pos);
    }

    // TODO ------------------------------------------------

    @Override
    public void save() {
        Path level = this.directory.resolve("level.dat");
        Path regionDir = this.directory.resolve("region");
        try {
            if (!Files.exists(this.directory)) {
                Files.createDirectory(this.directory);
            }

            if (!Files.exists(level)) {
                Files.createFile(level);
            }

            if (!Files.exists(regionDir)) {
                Files.createDirectory(regionDir);
            }

            Tag.Compound worldRoot = new Tag.Compound("");
            Tag.Compound worldData = new Tag.Compound("Data");
            worldRoot.putCompound(worldData);

            this.worldOptions.write(worldData);
            this.generatorOptions.write(worldData);
            this.weather.write(worldData);
            this.border.write(worldData);

            worldData.putString("LevelName", this.name);

            try (GZIPOutputStream stream = new GZIPOutputStream(new FileOutputStream(level.toFile()))) {
                worldRoot.write(new DataOutputStream(stream));
            }

            this.chunks.forEach(c -> {
                Region region = Region.getFile(c, true);
                try (DataOutputStream out = region.getChunkDataOutputStream(c.getX() & 31, c.getZ() & 31)) {
                    Tag.Compound rootChunk = new Tag.Compound("");
                    Tag.Compound chunkData = new Tag.Compound("Level");
                    c.write(chunkData);
                    rootChunk.putCompound(chunkData);
                    rootChunk.write(out);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}