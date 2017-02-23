/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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

import net.tridentsdk.base.Block;
import net.tridentsdk.base.ImmutableWorldVector;
import net.tridentsdk.base.Position;
import net.tridentsdk.server.concurrent.PoolSpec;
import net.tridentsdk.server.concurrent.ServerThreadPool;
import net.tridentsdk.server.world.opt.GenOptImpl;
import net.tridentsdk.server.world.opt.WorldOptImpl;
import net.tridentsdk.world.Chunk;
import net.tridentsdk.world.IntPair;
import net.tridentsdk.world.World;
import net.tridentsdk.world.opt.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

/**
 * Implementation class for
 * {@link net.tridentsdk.world.World}.
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
    private final String name;
    /**
     * The enclosing folder of the world directory
     */
    private final Path dir;
    /**
     * The implementation world options
     */
    private final WorldOptImpl worldOpts;
    /**
     * The implementation generator options
     */
    private final GenOptImpl genOpts;

    /**
     * Creates a new world with the given name, folder, and
     * creation options.
     *
     * @param name the name of the new world
     */
    public TridentWorld(String name, Path enclosing, WorldCreateSpec spec) {
        this.name = name;
        this.dir = enclosing;
        // this is only ok because we aren't passing the
        // instance to another thread viewable object
        this.worldOpts = new WorldOptImpl(this, spec);
        this.genOpts = spec.isDefault() ? new GenOptImpl() : new GenOptImpl(spec);
    }

    /**
     * Loads a new world with the given name and folder.
     *
     * @param name the name of the world
     * @param enclosing the enclosing folder
     */
    public TridentWorld(String name, Path enclosing) {
        this.name = name;
        this.dir = enclosing;
        // this is only ok because we aren't passing the
        // instance to another thread viewable object
        this.worldOpts = new WorldOptImpl(this, WorldCreateSpec.getDefaultOptions());
        this.genOpts = new GenOptImpl(new Object());
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
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getTime() {
        return 0;
    }

    @Override
    public WorldOpts getWorldOptions() {
        return this.worldOpts;
    }

    @Override
    public Weather getWeather() {
        return null;
    }

    @Override
    public GenOpts getGeneratorOptions() {
        return this.genOpts;
    }

    @Override
    public WorldBorder getBorder() {
        return null;
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

    public TridentChunk chunkAt(IntPair pair) {
        return this.getChunkAt(pair.getX(), pair.getZ());
    }

    @Override
    public Collection<? extends Chunk> getLoadedChunks() {
        return Collections.unmodifiableCollection(this.chunks.values());
    }

    @Override
    public Block getBlockAt(int x, int y, int z) {
        return new TridentBlock(new ImmutableWorldVector(this, x, y, z));
    }

    @Override
    public Block getBlockAt(Position pos) {
        return new TridentBlock(pos.toWorldVector());
    }

    @Override
    public Path getWorldDirectory() {
        return this.dir;
    }

    /**
     * Loads the world from the NBT level.dat format and
     * loads the appropriate spawn chunks.
     */
    public void load() {
        this.worldOpts.load();
    }

    @Override
    public void save() {
        this.worldOpts.save();
        this.genOpts.save();
    }
}
