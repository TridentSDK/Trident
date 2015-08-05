package net.tridentsdk.server.player;

import net.tridentsdk.world.ChunkLocation;

import javax.annotation.concurrent.GuardedBy;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;

/**
 * A fast table of chunk locations which possesses the known player chunks
 *
 * @author The TridentSDK Team
 */
public class StripedChunkTable {
    private final BiFunction<Integer, Integer, Integer> part;
    private final Lock[] stripes;
    private final Partition[] table;
    private final int partSize;

    // max == MAX_CHUNKS
    // size == 100, partition size
    // partitions =~ 5
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
        int aggr = 0;
        for (Partition p : table) {
            aggr += p.size();
        }

        return aggr;
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
            return p.chunks().add(location);
        } finally {
            lock.unlock();
        }
    }

    public Lock acquire(int partition) {
        return stripes[partition];
    }

    private class Partition {
        @GuardedBy("StripedChunkTable.stripes[(x & partitions/2)|(z * partitions/2)]")
        private final Set<ChunkLocation> knownChunks = new HashSet<>();

        public Set<ChunkLocation> chunks() {
            return knownChunks;
        }

        public int size() {
            return knownChunks.size();
        }
    }
}
