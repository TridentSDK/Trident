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
import net.tridentsdk.server.world.TridentChunk;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.world.ChunkLocation;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;


public class ChunkHandler {
    private final ConcurrentMap<ChunkLocation, CRefCounter> counters = new ConcurrentHashMap<>();
    private final TridentWorld world;

    public ChunkHandler(TridentWorld world) {
        this.world = world;
    }

    public void put(TridentChunk chunk) {
        counters.put(chunk.location(), CRefCounter.wrap(chunk));
    }

    public TridentChunk get(ChunkLocation location, boolean gen) {
        if (gen) {
            return counters.computeIfAbsent(location, k -> CRefCounter.wrap(world.generateChunk(k))).unwrap();
        } else {
            CRefCounter refCounter = counters.get(location);
            return refCounter == null ? null : refCounter.unwrap();
        }
    }

    public boolean apply(ChunkLocation location, Consumer<CRefCounter> consumer) {
        CRefCounter chunk = counters.get(location);
        if (chunk != null) {
            consumer.accept(chunk);
            return true;
        }

        return false;
    }

    public boolean tryRemove(ChunkLocation location) {
        if (location.x() < 7 && location.z() < 7) {
            // Spawn chunk TODO spawn radius
            return true;
        }

        CRefCounter chunk = counters.get(location);
        if (chunk == null) {
            return false;
        }

        if (!chunk.hasStrongRefs()) {
            TridentChunk c = chunk.unwrap();
            if (chunk.hasWeakRefs()) {

            }

            remove(location);
            c.unload();
        }

        return false;
    }

    public void remove(ChunkLocation location) {
        counters.remove(location);
    }

    public Set<ChunkLocation> keys() {
        return counters.keySet();
    }

    public Collection<TridentChunk> values() {
        Collection<TridentChunk> chunks = Lists.newArrayList();
        counters.values().stream().forEach(c -> chunks.add(c.unwrap()));
        return chunks;
    }

    public int size() {
        return counters.size();
    }
}