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
import net.tridentsdk.server.world.opt.GenOptImpl;
import net.tridentsdk.server.world.opt.WorldOptImpl;
import net.tridentsdk.world.Chunk;
import net.tridentsdk.world.World;
import net.tridentsdk.world.opt.*;

import javax.annotation.concurrent.ThreadSafe;
import java.nio.file.Path;

/**
 * Implementation class for
 * {@link net.tridentsdk.world.World}.
 */
@ThreadSafe
public class TridentWorld implements World {
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
        this.worldOpts = new WorldOptImpl(this, WorldCreateSpec.defaultOpts());
        this.genOpts = new GenOptImpl(new Object());
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public int time() {
        return 0;
    }

    @Override
    public WorldOpts opts() {
        return this.worldOpts;
    }

    @Override
    public Weather weather() {
        return null;
    }

    @Override
    public GenOpts genOpts() {
        return this.genOpts;
    }

    @Override
    public WorldBorder border() {
        return null;
    }

    @Override
    public Chunk chunkAt(int x, int z) {
        return null;
    }

    @Override
    public Block blockAt(int x, int y, int z) {
        // the reason we switch to AND operator instead of
        // using MOD is because chunk-rel coordinates are
        // never negative
        return this.chunkAt(x % 16, z % 16).blockAt(x & 15, y, z & 15);
    }

    @Override
    public Path dir() {
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