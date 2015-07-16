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
package net.tridentsdk.server.bench;

import com.google.common.collect.Sets;
import net.tridentsdk.Position;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.traits.EntityProperties;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.util.Vector;
import net.tridentsdk.util.WeakEntity;
import net.tridentsdk.world.World;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import javax.annotation.concurrent.GuardedBy;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
# Run progress: 0.00% complete, ETA 00:00:16
# Warmup: 20 iterations, 200 ms each
# Measurement: 20 iterations, 200 ms each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.bench.MapTest.regPut
# VM invoker: /usr/lib/jvm/java-8-oracle/jre/bin/java
# VM options: -Didea.launcher.port=7533 -Didea.launcher.bin.path=/home/agenttroll/idea-IU-139.659.2/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 135.931 ns/op
# Warmup Iteration   2: 109.168 ns/op
# Warmup Iteration   3: 114.800 ns/op
# Warmup Iteration   4: 118.234 ns/op
# Warmup Iteration   5: 116.170 ns/op
# Warmup Iteration   6: 115.652 ns/op
# Warmup Iteration   7: 116.000 ns/op
# Warmup Iteration   8: 118.779 ns/op
# Warmup Iteration   9: 112.300 ns/op
# Warmup Iteration  10: 115.235 ns/op
# Warmup Iteration  11: 114.183 ns/op
# Warmup Iteration  12: 117.252 ns/op
# Warmup Iteration  13: 117.345 ns/op
# Warmup Iteration  14: 116.096 ns/op
# Warmup Iteration  15: 116.779 ns/op
# Warmup Iteration  16: 118.817 ns/op
# Warmup Iteration  17: 116.581 ns/op
# Warmup Iteration  18: 115.478 ns/op
# Warmup Iteration  19: 116.367 ns/op
# Warmup Iteration  20: 119.083 ns/op
Iteration   1: 119.241 ns/op
Iteration   2: 121.449 ns/op
Iteration   3: 114.077 ns/op
Iteration   4: 114.320 ns/op
Iteration   5: 116.197 ns/op
Iteration   6: 117.656 ns/op
Iteration   7: 115.279 ns/op
Iteration   8: 116.227 ns/op
Iteration   9: 114.311 ns/op
Iteration  10: 117.191 ns/op
Iteration  11: 118.338 ns/op
Iteration  12: 121.635 ns/op
Iteration  13: 117.848 ns/op
Iteration  14: 114.392 ns/op
Iteration  15: 117.701 ns/op
Iteration  16: 117.385 ns/op
Iteration  17: 115.795 ns/op
Iteration  18: 113.739 ns/op
Iteration  19: 117.870 ns/op
Iteration  20: 116.213 ns/op

Result: 116.843 ±(99.9%) 1.961 ns/op [Average]
  Statistics: (min, avg, max) = (113.739, 116.843, 121.635), stdev = 2.258
  Confidence interval (99.9%): [114.883, 118.804]


# Run progress: 50.00% complete, ETA 00:00:16
# Warmup: 20 iterations, 200 ms each
# Measurement: 20 iterations, 200 ms each
# Threads: 4 threads, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: net.tridentsdk.server.bench.MapTest.weakPut
# VM invoker: /usr/lib/jvm/java-8-oracle/jre/bin/java
# VM options: -Didea.launcher.port=7533 -Didea.launcher.bin.path=/home/agenttroll/idea-IU-139.659.2/bin -Dfile.encoding=UTF-8
# Fork: 1 of 1
# Warmup Iteration   1: 142.725 ns/op
# Warmup Iteration   2: 133.043 ns/op
# Warmup Iteration   3: 113.384 ns/op
# Warmup Iteration   4: 114.110 ns/op
# Warmup Iteration   5: 119.470 ns/op
# Warmup Iteration   6: 112.012 ns/op
# Warmup Iteration   7: 115.971 ns/op
# Warmup Iteration   8: 115.614 ns/op
# Warmup Iteration   9: 116.167 ns/op
# Warmup Iteration  10: 115.111 ns/op
# Warmup Iteration  11: 119.042 ns/op
# Warmup Iteration  12: 114.899 ns/op
# Warmup Iteration  13: 115.380 ns/op
# Warmup Iteration  14: 116.907 ns/op
# Warmup Iteration  15: 114.587 ns/op
# Warmup Iteration  16: 112.821 ns/op
# Warmup Iteration  17: 111.492 ns/op
# Warmup Iteration  18: 114.921 ns/op
# Warmup Iteration  19: 113.915 ns/op
# Warmup Iteration  20: 116.407 ns/op
Iteration   1: 116.250 ns/op
Iteration   2: 115.127 ns/op
Iteration   3: 110.731 ns/op
Iteration   4: 112.699 ns/op
Iteration   5: 116.030 ns/op
Iteration   6: 116.412 ns/op
Iteration   7: 112.575 ns/op
Iteration   8: 117.251 ns/op
Iteration   9: 114.431 ns/op
Iteration  10: 118.779 ns/op
Iteration  11: 113.391 ns/op
Iteration  12: 116.275 ns/op
Iteration  13: 115.658 ns/op
Iteration  14: 116.980 ns/op
Iteration  15: 114.209 ns/op
Iteration  16: 115.972 ns/op
Iteration  17: 120.676 ns/op
Iteration  18: 113.888 ns/op
Iteration  19: 119.488 ns/op
Iteration  20: 116.149 ns/op

Result: 115.648 ±(99.9%) 2.093 ns/op [Average]
  Statistics: (min, avg, max) = (110.731, 115.648, 120.676), stdev = 2.410
  Confidence interval (99.9%): [113.556, 117.741]


# Run complete. Total time: 00:00:32

Benchmark                   Mode   Samples        Score  Score error    Units
n.t.s.b.MapTest.regPut      avgt        20      116.843        1.961    ns/op
n.t.s.b.MapTest.weakPut     avgt        20      115.648        2.093    ns/op
 */
@State(Scope.Benchmark)
public class MapTest {
    private static final Map<Object, RefList> REF_LIST_MAP = new HashMap<>();
    private static final Lock mapLock = new ReentrantLock();

    private static final Object key = new Object();
    private static final RefList LIST = RefList.newNode(WeakEntity.of(new Entity() {
        @Override public void teleport(double x, double y, double z) {}
        @Override public void teleport(Entity entity) {}
        @Override public void teleport(Position location) {}
        @Override public World getWorld() {return null;}
        @Override public Position getPosition() {return null;}
        @Override public Vector getVelocity() {return null;}
        @Override public void setVelocity(Vector vector) {}
        @Override public boolean isOnGround() {return false;}
        @Override public Set<Entity> getNearbyEntities(double radius) {return null;}
        @Override public String getDisplayName() {return null;}
        @Override public void setDisplayName(String name) {}
        @Override public boolean isNameVisible() {return false;}
        @Override public boolean isSilent() {return false;}
        @Override public int getEntityId() {return 0;}
        @Override public UUID getUniqueId() {return null;}
        @Override public void remove() {}
        @Override public Entity getPassenger() {return null;}
        @Override public void setPassenger(Entity entity) {}
        @Override public void eject() {}
        @Override public EntityType getType() {return null;}
        @Override public void applyProperties(EntityProperties properties) {}
    }), null);

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + MapTest.class.getSimpleName() + ".*")
                .mode(Mode.AverageTime)
                .measurementIterations(20)
                .measurementTime(TimeValue.milliseconds(200))
                .warmupIterations(20)
                .warmupTime(TimeValue.milliseconds(200))
                .threads(4)
                .forks(1)
                .timeUnit(TimeUnit.NANOSECONDS)
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    public void weakPut() {
        add(key, LIST);
    }

    @Benchmark
    public void regPut() {
        mapLock.lock();
        try {
            REF_LIST_MAP.put(key, LIST);
        } finally {
            mapLock.unlock();
        }
    }

    @GuardedBy("lock")
    private RefEntry[] entries = new RefEntry[16];

    @GuardedBy("lock")
    private int length;

    // Locking mechanisms
    private final Lock lock = new ReentrantLock();

    private void add(Object key, RefList value) {
        lock.lock();
        try {
            int hash = hash(key);
            RefEntry toSet = this.search(key, value, hash);
            toSet.setHash(hash);
            toSet.setVal(value);
        } finally {
            lock.unlock();
        }
    }

    private RefList get(Object key) {
        lock.lock();
        try {
            int hash = hash(key);
            RefEntry node = this.loop(key, this.entries[hash]);
            if (node == null) return null;
            else return node.val();
        } finally {
            lock.unlock();
        }
    }

    private void remove(Object key) {
        try {
            RefEntry head = this.entries[hash(key)];
            if (head == null) return;

            this.remove(key, head);
        } finally {
            lock.unlock();
        }
    }

    private Set<RefEntry> entries() {
        Set<RefEntry> set = Sets.newHashSet();
        for (RefEntry entry : entries) {
            if (entry == null) continue;
            set.add(entry);
        }
        return set;
    }

    private void resize() {
        lock.lock();
        try {
            if (this.length > entries.length - 2) {
                int newLen = entries.length * 2;

                RefEntry[] resize = new RefEntry[newLen];
                System.arraycopy(entries, 0, resize, 0, length);
                this.entries = resize;
            }
        } finally {
            lock.unlock();
        }
    }

    private int hash(Object key) {
        long h = key.hashCode();
        h = (h >> 16 ^ h) * 0x33L;
        h = (h >> 16 ^ h) * 0x33L;
        h = h >> 16 ^ h;

        return (int) (h % (long) entries.length);
    }
    
    private RefEntry loop(Object key, RefEntry head) {
        RefEntry tail = head;
        while (tail != null) {
            if (tail.key().equals(key))
                return tail;
            tail = tail.next();
        }

        return null;
    }

    private RefEntry search(Object k, RefList v, int hash) {
        RefEntry head = this.entries[hash];
        RefEntry tail = this.loop(k, head);
        if (tail != null) return tail;
        else return this.create(k, null, v, hash);
    }

    private RefEntry create(Object k, RefEntry previous, RefList v, int hash) {
        RefEntry node = new RefEntry(k, v, hash);
        if (previous == null) {
            this.entries[hash] = node;
            this.length++;
            this.resize();
            return node;
        }

        previous.setNext(node);
        this.length++;
        this.resize();
        return node;
    }

    private void remove(Object k, RefEntry head) {
        RefEntry leading = head;
        RefEntry tail = leading == null ? null : leading.next();

        while (tail != null) {
            if (tail.key().equals(k)) {
                leading.setNext(tail.next());
                this.length--;
                return;
            }

            tail = tail.next();
            leading = leading.next();
        }

        if (leading != null && leading.key().equals(k))
            this.entries[leading.hash()] = null;

        this.length--;
    }

    private static class RefEntry {
        private final Object key;

        @GuardedBy("RefList.lock")
        private RefList list;
        @GuardedBy("RefList.lock")
        private int hash;
        @GuardedBy("RefList.lock")
        private RefEntry next;

        private RefEntry(Object key, RefList entity, int hash) {
            this.key = key;
            this.list = entity;
            this.hash = hash;
        }

        public static RefEntry newEntry(Object key, RefList entity, int hash) {
            return new RefEntry(key, entity, hash);
        }

        public Object key() {
            return this.key;
        }

        public RefList val() {
            return this.list;
        }

        public void setVal(RefList list) {
            this.list = list;
        }

        public int hash() {
            return this.hash;
        }

        public void setHash(int hash) {
            this.hash = hash;
        }

        public RefEntry next() {
            return this.next;
        }

        public void setNext(RefEntry next) {
            this.next = next;
        }
    }

    // A set of references assigned to a particular entity
    private static class RefList implements Iterable<WeakEntity> {
        private static final Entity NULL = new Entity() {
            @Override public void teleport(double x, double y, double z) {}
            @Override public void teleport(Entity entity) {}
            @Override public void teleport(Position location) {}
            @Override public World getWorld() {return null;}
            @Override public Position getPosition() {return null;}
            @Override public Vector getVelocity() {return null;}
            @Override public void setVelocity(Vector vector) {}
            @Override public boolean isOnGround() {return false;}
            @Override public Set<Entity> getNearbyEntities(double radius) {return null;}
            @Override public String getDisplayName() {return null;}
            @Override public void setDisplayName(String name) {}
            @Override public boolean isNameVisible() {return false;}
            @Override public boolean isSilent() {return false;}
            @Override public int getEntityId() {return 0;}
            @Override public UUID getUniqueId() {return null;}
            @Override public void remove() {}
            @Override public Entity getPassenger() {return null;}
            @Override public void setPassenger(Entity entity) {}
            @Override public void eject() {}
            @Override public EntityType getType() {return null;}
            @Override public void applyProperties(EntityProperties properties) {}

            @Override
            public int hashCode() {
                return 0;
            }

            @Override
            public boolean equals(Object obj) {
                return obj == null;
            }
        };

        @GuardedBy("lock")
        private final Set<WeakEntity> weakEntities;
        private final Object finder;
        private final Object lock = new Object();

        private RefList(final WeakEntity entity, RefList node) {
            this.finder = new Object() {
                @Override
                public int hashCode() {
                    return entity.hashCode();
                }

                @Override
                public boolean equals(Object obj) {
                    return entity.equals(obj);
                }
            };

            Set<WeakEntity> original;
            if (node == null)
                original = Sets.newHashSet();
            else
                original = node.weakEntities;

            if (original == null)
                original = Sets.newHashSet();
            original.add(entity);

            weakEntities = original;
        }

        public static RefList newNode(WeakEntity entity, RefList node) {
            return new RefList(entity, node);
        }

        // Removes the references which are empty
        public void clean() throws InterruptedException {
            synchronized (lock) {
                weakEntities.stream().filter(entity -> entity.isNull()).forEach(weakEntities::remove);
            }
        }

        // Removes a specified entity
        public void clear(WeakEntity entity) {
            synchronized (lock) {
                weakEntities.remove(entity);
            }
        }

        public Set<WeakEntity> refs() {
            synchronized (lock) {
                return weakEntities;
            }
        }

        @Override
        public Iterator<WeakEntity> iterator() {
            synchronized (lock) {
                return weakEntities.iterator();
            }
        }

        // Used to reference the entity without actually using it
        public Object finder() {
            return this.finder;
        }
    }
}
