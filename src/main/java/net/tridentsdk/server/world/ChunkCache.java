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

import com.google.common.collect.Lists;
import io.netty.util.internal.chmv8.ConcurrentHashMapV8;
import net.tridentsdk.concurrent.HeldValueLatch;
import net.tridentsdk.docs.AccessNoDoc;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.world.ChunkLocation;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

@AccessNoDoc
class ChunkCache {
    private final ConcurrentMap<ChunkLocation, HeldValueLatch<TridentChunk>> cachedChunks = new ConcurrentHashMapV8<>();
    private final TridentWorld world;

    public ChunkCache(TridentWorld world) {
        this.world = world;
    }

    public void put(ChunkLocation location, TridentChunk chunk) {
        HeldValueLatch<TridentChunk> latch = cachedChunks.get(location);
        if (latch == null)
            latch = HeldValueLatch.create();
        if (!latch.hasValue())
            latch.countDown(chunk);
        else {
            latch = HeldValueLatch.create();
            latch.countDown(chunk);
        }

        cachedChunks.put(location, latch);
    }

    public TridentChunk get(ChunkLocation location, boolean gen) {
        while (true) {
            HeldValueLatch<TridentChunk> value = cachedChunks.get(location);
            if (value == null) {
                if (!gen) return null;

                HeldValueLatch<TridentChunk> latch = HeldValueLatch.create();
                value = cachedChunks.putIfAbsent(location, latch);
                if (value == null) {
                    value = latch;
                    value.countDown(world.generateChunk(location));
                } else return null;
            }

            if (!value.hasValue() && !gen)
                return null;

            try {
                return value.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void retain(Set<ChunkLocation> set) {
        for (ChunkLocation l : keys()) {
            if (!set.contains(l)) {
                cachedChunks.remove(l);
            }
        }
    }

    public Set<ChunkLocation> keys() {
        return cachedChunks.keySet();
    }

    public Collection<TridentChunk> values() {
        Collection<TridentChunk> chunks = Lists.newArrayList();
        for (HeldValueLatch<TridentChunk> chunk : cachedChunks.values()) {
            if (chunk.hasValue()) {
                try {
                    chunks.add(chunk.await());
                } catch (InterruptedException e) {
                    TridentLogger.error(e);
                }
            }
        }

        return chunks;
    }
}
