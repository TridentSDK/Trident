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

import net.tridentsdk.server.util.Long2ReferenceOpenHashMap;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Map of loaded chunks.
 *
 * <p>This class makes concurrency handing easier on the
 * world.</p>
 */
public class ChunkMap implements Iterable<TridentChunk> {
    /**
     * The lock guarding the chunk map
     */
    private final Object lock = new Object();
    /**
     * The actual map of chunks
     */
    @GuardedBy("lock")
    private final Long2ReferenceOpenHashMap<TridentChunk> chunks = new Long2ReferenceOpenHashMap<>();
    /**
     * The world holding the chunks in this map
     */
    private final TridentWorld world;

    /**
     * Creates a new ChunkMap for the given world.
     *
     * @param world the world to hold chunks
     */
    public ChunkMap(TridentWorld world) {
        this.world = world;
    }

    /**
     * Obtains the chunk at the given location and
     * determines whether a chunk will be generated if it
     * does not exist yet.
     *
     * @param x the x coordinate
     * @param z the z coordinate
     * @param gen {@code true} to generate if non-existant
     * @return the chunk, or {@code null}
     */
    public TridentChunk get(int x, int z, boolean gen) {
        long key = (long) x << 32 | z & 0xFFFFFFFFL;
        boolean doGenerate = false;
        TridentChunk chunk;

        synchronized (this.lock) {
            chunk = this.chunks.get(key);
            if ((chunk == null || !chunk.canUse()) && gen) {
                chunk = new TridentChunk(this.world, x, z);
                this.chunks.put(key, chunk);

                doGenerate = true;
            }
        }

        if (doGenerate) {
            chunk.generate();
        }

        if (chunk != null) {
            return chunk.waitReady();
        } else {
            return null;
        }
    }

    /**
     * Removes the chunk at the given coordinates.
     *
     * @param x the chunk X coordinate
     * @param z the chunk Z coordinate
     * @return the chunk that was removed, or {@code null}
     * if nothing happened
     */
    public TridentChunk remove(int x, int z) {
        long key = (long) x << 32 | z & 0xFFFFFFFFL;

        synchronized (this.lock) {
            return this.chunks.remove(key);
        }
    }

    /**
     * All of the loaded chunks.
     *
     * @return the values of the chunk map
     */
    public Collection<TridentChunk> values() {
        synchronized (this.lock) {
            return this.chunks.values();
        }
    }

    @Nonnull
    @Override
    public Iterator<TridentChunk> iterator() {
        return this.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super TridentChunk> action) {
        synchronized (this.lock) {
            for (TridentChunk chunk : this) {
                action.accept(chunk);
            }
        }
    }
}