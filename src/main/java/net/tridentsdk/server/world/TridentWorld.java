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
import net.tridentsdk.world.Chunk;
import net.tridentsdk.world.World;
import net.tridentsdk.world.opt.GenOpts;
import net.tridentsdk.world.opt.Weather;
import net.tridentsdk.world.opt.WorldBorder;
import net.tridentsdk.world.opt.WorldOpts;

import javax.annotation.concurrent.ThreadSafe;

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
     * Creates a new world with the given name.
     *
     * @param name the name of the new world
     */
    private TridentWorld(String name) {
        this.name = name;
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
        return null;
    }

    @Override
    public Weather weather() {
        return null;
    }

    @Override
    public GenOpts genOpts() {
        return null;
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
}