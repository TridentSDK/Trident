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

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;

import javax.annotation.concurrent.GuardedBy;
import java.util.Collection;
import java.util.concurrent.locks.StampedLock;

/**
 * Map of loaded chunks.
 *
 * <p>This class makes concurrency handing easier on the
 * world.</p>
 */
public class ChunkMap {
    /**
     * The lock guarding the chunk map
     */
    private final StampedLock lock = new StampedLock();
    /**
     * The actual map of chunks
     */
    @GuardedBy("lock")
    private final TLongObjectMap<TridentChunk> chunks = new TLongObjectHashMap<TridentChunk>() {
        @Override
        public TridentChunk get(long key) {
            TridentChunk chunk = super.get(key);
            if (chunk != null) {
                ChunkMap.this.lock.tryUnlockRead();
                return chunk.waitReady();
            }

            return null;
        }
    };
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
        long key = (long) x | (long) z << 32;
        long stamp = this.lock.readLock();
        try {
            TridentChunk chunk = this.chunks.get(key);
            if (chunk == null && gen) {
                chunk = new TridentChunk(this.world, x, z);
                chunk.generate();

                stamp = this.lock.tryConvertToWriteLock(stamp);
                if (stamp == 0) {
                    stamp = this.lock.writeLock();
                }

                try {
                    this.chunks.put(key, chunk);
                } finally {
                    this.lock.unlockWrite(stamp);
                }
            }

            return chunk;
        } finally {
            this.lock.tryUnlockRead();
        }
    }

    /**
     * All of the loaded chunks.
     *
     * @return the values of the chunk map
     */
    public Collection<TridentChunk> values() {
        long stamp = this.lock.readLock();
        try {
            return this.chunks.valueCollection();
        } finally {
            this.lock.unlockRead(stamp);
        }
    }
}