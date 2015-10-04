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

import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Allows for higher throughput chunk memory I/O for concurrent access
 *
 * @author The TridentSDK Team
 */
public class ConcurrentSectionTable {
    private final ChunkSection[] sections = new ChunkSection[16];
    private final StampedLock[] locks = new StampedLock[16];

    public ConcurrentSectionTable() {
        for (int i = 0; i < 16; i++) {
            sections[i] = new ChunkSection((byte) i);
            locks[i] = new StampedLock();
        }
    }

    public void readLockFully() {
        for (StampedLock lock : locks) {
            lock.readLock();
        }
    }

    public void releaseRead() {
        for (StampedLock lock : locks) {
            lock.tryUnlockRead();
        }
    }

    public void writeLockFully() {
        for (StampedLock lock : locks) {
            lock.writeLock();
        }
    }

    public void releaseWrite() {
        for (StampedLock lock : locks) {
            lock.tryUnlockWrite();
        }
    }

    public ChunkSection get(int i) {
        return sections[i];
    }

    public void set(int i, ChunkSection section) {
        sections[i] = section;
    }

    public ChunkSection acquire(int i) {
        StampedLock lock = locks[i];
        long stamp = lock.readLock();
        try {
            return sections[i];
        } finally {
            lock.unlockRead(stamp);
        }
    }

    public void read(int i, Consumer<ChunkSection> consumer) {
        StampedLock lock = locks[i];
        long stamp = lock.readLock();
        try {
            consumer.accept(sections[i]);
        } finally {
            lock.unlockRead(stamp);
        }
    }

    public <T> T read(int i, Function<ChunkSection, T> func) {
        StampedLock lock = locks[i];
        long stamp = lock.readLock();
        try {
            return func.apply(sections[i]);
        } finally {
            lock.unlockRead(stamp);
        }
    }

    public void write(int i, Consumer<ChunkSection> func) {
        StampedLock lock = locks[i];
        long stamp = lock.writeLock();
        try {
            func.accept(sections[i]);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public <T> T write(int i, Function<ChunkSection, T> consumer) {
        StampedLock lock = locks[i];
        long stamp = lock.writeLock();
        try {
            return consumer.apply(sections[i]);
        } finally {
            lock.unlockWrite(stamp);
        }
    }
}
