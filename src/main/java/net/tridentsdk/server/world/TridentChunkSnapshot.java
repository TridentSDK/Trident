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


import net.tridentsdk.base.Tile;
import net.tridentsdk.meta.nbt.CompoundTag;
import net.tridentsdk.world.Chunk;
import net.tridentsdk.world.ChunkLocation;
import net.tridentsdk.world.ChunkSnapshot;
import net.tridentsdk.world.World;

import java.util.List;

public class TridentChunkSnapshot implements ChunkSnapshot {
    private final TridentWorld world;
    private final ChunkLocation location;
    private final int lastFileAccess;

    private final long lastModified;
    private final long inhabitedTime;
    private final byte lightPopulated;
    private final byte terrainPopulated;
    private final CompoundTag[] sections;

    public TridentChunkSnapshot(TridentWorld world, ChunkLocation location, List<CompoundTag> list, int lastFileAccess,
                                long lastModified, long inhabitedTime, byte lightPopulated, byte terrainPopulated) {
        this.world = world;
        this.location = location;
        this.lastFileAccess = lastFileAccess;
        this.lastModified = lastModified;
        this.inhabitedTime = inhabitedTime;
        this.lightPopulated = lightPopulated;
        this.terrainPopulated = terrainPopulated;
        sections = list.toArray(new CompoundTag[list.size()]);
    }

    @Override
    public void load(Chunk chunk) {
        // TODO
    }

    @Override
    public void load() {
        // TODO
    }

    @Override
    public void generate() {
    }

    @Override
    public ChunkLocation getLocation() {
        return location;
    }

    @Override
    public int getX() {
        return location.getX();
    }

    @Override
    public int getZ() {
        return location.getZ();
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public Tile getTileAt(int relX, int y, int relZ) {
        return null;
    }

    @Override
    public ChunkSnapshot snapshot() {
        return this;
    }
}
