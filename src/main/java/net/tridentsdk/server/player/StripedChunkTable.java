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
package net.tridentsdk.server.player;

import net.tridentsdk.world.ChunkLocation;

import javax.annotation.concurrent.GuardedBy;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;

/**
 * A fast table of chunk locations which possesses the known player chunks
 *
 * @author The TridentSDK Team
 */ // heehee didn't work but ok
public class StripedChunkTable {
    private final LongAdder length = new LongAdder();
    private final BiFunction<Integer, Integer, Integer> part;
    private final Lock[] stripes;
    private final Partition[] table;
    private final int partSize;

    public StripedChunkTable(int max, int size) {
        int partitions = max / size + 1;
        int stripeHalf = partitions / 2;

        stripes = new Lock[partitions];
        table = new Partition[partitions];
        for (int i = 0; i < partitions; i++) {
            stripes[i] = new ReentrantLock();
            table[i] = new Partition();
        }

        partSize = size;
        part = (x, z) -> (x & stripeHalf) | (z & stripeHalf);
    }

    public int tableLength() {
        return table.length;
    }

    public int partSize() {
        return partSize;
    }

    public int size() {
        return length.intValue();
    }

    public Set<ChunkLocation> at(int index) {
        Lock lock = stripes[index];
        lock.lock();
        return table[index].chunks();
    }

    public boolean add(ChunkLocation location) {
        int partition = part.apply(location.x(), location.z());

        // We are able to get away with this because the table array is immutable
        Partition p = table[partition];
        Lock lock = stripes[partition];
        lock.lock();
        try {
            if (p.chunks().add(location)) {
                length.increment();
                return true;
            } else return false;
        } finally {
            lock.unlock();
        }
    }

    public void remove(Iterator<ChunkLocation> iterator) {
        iterator.remove();
        length.decrement();
    }

    public Lock acquire(int partition) {
        return stripes[partition];
    }

    private class Partition {
        @GuardedBy("StripedChunkTable.stripes[(x & partitions/2)|(z * partitions/2)]")
        private final HashSet<ChunkLocation> knownChunks = new HashSet<>();

        public Set<ChunkLocation> chunks() {
            return knownChunks;
        }

        public int size() {
            return knownChunks.size();
        }
    }
}
