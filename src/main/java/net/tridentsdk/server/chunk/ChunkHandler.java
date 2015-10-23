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
package net.tridentsdk.server.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.tridentsdk.server.world.TridentChunk;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.world.ChunkLocation;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Manages the chunks stored in memory per world
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class ChunkHandler {
    @GuardedBy("counters")
    private final HashMap<ChunkLocation, CRefCounter> counters = Maps.newHashMap();
    private final TridentWorld world;

    /**
     * Creates a new chunk handler to manage the chunks of the provided world
     *
     * @param world the world to manage chunks for
     */
    public ChunkHandler(TridentWorld world) {
        this.world = world;
    }

    /**
     * Places a chunk into the collection of in-memory chunks
     *
     * @param chunk the chunk to add
     */
    public void put(TridentChunk chunk) {
        synchronized (counters) {
            counters.put(chunk.location(), CRefCounter.wrap(chunk));
        }
    }

    /**
     * Obtains the chunk at the given location in the world, generating if given to do so
     *
     * @param location the location to obtain the chunk
     * @param gen      {@code true} to generate a new chunk if no chunk exists
     * @return the chunk at the given location, or {@code null} if it doesn't exist and {@code gen} is false
     */
    public TridentChunk get(ChunkLocation location, boolean gen) {
        if (gen) {
            synchronized (counters) {
                CRefCounter counter = counters.get(location);
                if (counter == null) {
                    return world.generateChunk(location);
                } else return counter.unwrap();
            }
        } else {
            CRefCounter refCounter = get(location);
            return refCounter == null ? null : refCounter.unwrap();
        }
    }

    /**
     * Obtains the chunk reference counter at the specified location
     *
     * @param location the location to obtain the counter
     * @return the counter at the location, or {@code null} if it doesn't exist
     */
    public CRefCounter get(ChunkLocation location) {
        synchronized (counters) {
            return counters.get(location);
        }
    }

    /**
     * Obtains the chunk reference counter and applies a transformation function
     *
     * @param location the location or obtain the chunk reference counter
     * @param consumer the transformation function
     * @return {@code true} to indicate that the chunk was successfully retrieved and transformed
     */
    public boolean apply(ChunkLocation location, Consumer<CRefCounter> consumer) {
        CRefCounter chunk = get(location);
        if (chunk != null) {
            consumer.accept(chunk);
            return true;
        }

        return false;
    }

    /**
     * Attempts to remove the chunk from memory and save it
     *
     * <p>This method returns {@code false} if:
     * <ul>
     *     <li>The chunk is not loaded</li>
     *     <li>The chunk still has strong references</li>
     * </ul></p>
     *
     * @param location the location to remove the chunk
     * @return {@code true} to signify that the collection was modified as a result of this operation
     */
    public boolean tryRemove(ChunkLocation location) {
        if (location.x() < 7 && location.z() < 7) {
            // Spawn chunk TODO spawn radius
            return true;
        }

        synchronized (counters) {
            CRefCounter chunk = get(location);
            if (chunk == null) {
                return false;
            }

            if (!chunk.hasStrongRefs()) {
                TridentChunk c = chunk.unwrap();
                if (chunk.hasWeakRefs()) {
                    // TODO remove weak referencing items
                }

                c.unload();
                remove(location);
                return true;
            }
        }

        return false;
    }

    /**
     * Releases the reference counters associated with the chunks that are specified in the set given
     *
     * @param chunkSet the set of chunks to release references to, given that they exist in this cache
     */
    public void releaseReferences(ChunkLocationSet chunkSet) {
        synchronized (counters) {
            for (ChunkLocation location : chunkSet.locations()) {
                CRefCounter counter = get(location);
                if (counter != null) {
                    counter.releaseStrong();
                }
            }
        }
    }

    /**
     * Manually removes the chunk from the collection without running any cleanup code
     *
     * @param location the location to remove the chunk from
     */
    public void remove(ChunkLocation location) {
        synchronized (counters) {
            counters.remove(location);
        }
    }

    /**
     * Obtains the set of chunk locations that have already been loaded
     *
     * @return the set of loaded chunk locations
     */
    public Set<ChunkLocation> keys() {
        synchronized (counters) {
            return counters.keySet();
        }
    }

    /**
     * Obtains the chunks that have been loaded into memory
     *
     * @return the collection of loaded in-memory chunks
     */
    public Collection<TridentChunk> values() {
        Collection<TridentChunk> chunks = Lists.newArrayList();
        synchronized (counters) {
            counters.values().stream().forEach(c -> chunks.add(c.unwrap()));
        }
        return chunks;
    }

    /**
     * Obtains the amount of loaded chunks
     *
     * @return the amount of loaded chunks
     */
    public int size() {
        synchronized (counters) {
            return counters.size();
        }
    }
}