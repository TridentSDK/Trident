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

import net.tridentsdk.base.Block;
import net.tridentsdk.base.BoundingBox;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.meta.block.Tile;
import net.tridentsdk.meta.nbt.CompoundTag;
import net.tridentsdk.world.Chunk;
import net.tridentsdk.world.ChunkLocation;
import net.tridentsdk.world.ChunkSnapshot;
import net.tridentsdk.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

public class TridentChunkSnapshot implements ChunkSnapshot {
    private final TridentWorld world;
    private final ChunkLocation location;
    private final CompoundTag tag;

    public TridentChunkSnapshot(TridentWorld world, TridentChunk chunk) {
        this.world = world;
        this.location = chunk.location();
        this.tag = chunk.asNbt();
    }

    @Override
    public void apply(Chunk chunk) {
        ((TridentChunk) chunk).load(tag);
    }

    @Override
    public void apply() {
        ((TridentChunk) world().chunkAt(location(), true)).load(tag);
    }

    @Override
    public Set<Entity> entities() {
        TridentChunk chunk = new TridentChunk((TridentWorld) world(), location);
        chunk.load(tag);
        return chunk.entities();
    }

    @Override
    public Collection<Tile> tiles() {
        TridentChunk chunk = new TridentChunk((TridentWorld) world(), location);
        chunk.load(tag);
        return chunk.tiles();
    }

    @Override
    public void generate() {
        apply();
    }

    @Override
    public boolean load() {
        return false;
    }

    @Override
    public boolean isLoaded() {
        return false;
    }

    @Override
    public ChunkLocation location() {
        return location;
    }

    @Override
    public int x() {
        return location.x();
    }

    @Override
    public int z() {
        return location.z();
    }

    @Override
    public World world() {
        return world;
    }

    @Override
    public Block blockAt(int relX, int y, int relZ) {
        TridentChunk chunk = new TridentChunk(((TridentWorld) world()), location);
        chunk.load(tag);
        return chunk.blockAt(relX, y, relZ);
    }

    @Override
    public ChunkSnapshot snapshot() {
        return this;
    }

    @Override
    public void unload() {
        throw new UnsupportedOperationException("Cannot unload a snapshot");
    }

    public ArrayList<Entity> getEntities(Entity exclude, BoundingBox boundingBox, Predicate<? super Entity> predicate){
        return new ArrayList<>();
    }
}
