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
package net.tridentsdk.server.config;

import com.google.common.collect.Maps;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;

/**
 * Yet another dumb class without any real purpose that is
 * made necessary simply for no apparent reason
 *
 * since I am lazy I don't intend on adding any unnecessary
 * bs code that won't be used, so this really isn't a real
 * "map"
 *
 * all these methods are self-explanatory, there is no use
 * for documenting them
 */
@ThreadSafe
public class ConcurrentLinkedStringMap<V> {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final LinkedHashMap<String, V> map = Maps.newLinkedHashMap();

    public void put(String key, V value) {
        Lock lock = this.lock.writeLock();
        lock.lock();
        try {
            map.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    public V remove(String key) {
        Lock lock = this.lock.writeLock();
        lock.lock();
        try {
            return map.remove(key);
        } finally {
            lock.unlock();
        }
    }

    public V get(String key) {
        Lock lock = this.lock.readLock();
        lock.lock();
        try {
            return map.get(key);
        } finally {
            lock.unlock();
        }
    }

    public boolean containsKey(String key) {
        Lock lock = this.lock.readLock();
        lock.lock();
        try {
            return map.containsKey(key);
        } finally {
            lock.unlock();
        }
    }

    public Set<String> keySet() {
        Lock lock = this.lock.readLock();
        lock.lock();
        try {
            return map.keySet();
        } finally {
            lock.unlock();
        }
    }

    public Collection<V> values() {
        Lock lock = this.lock.readLock();
        lock.lock();
        try {
            return map.values();
        } finally {
            lock.unlock();
        }
    }

    public void forEach(BiConsumer<String, V> consumer) {
        Lock lock = this.lock.readLock();
        lock.lock();
        try {
            map.forEach(consumer);
        } finally {
            lock.unlock();
        }
    }

    public Set<Map.Entry<String, V>> entrySet() {
        Lock lock = this.lock.readLock();
        lock.lock();
        try {
            return map.entrySet();
        } finally {
            lock.unlock();
        }
    }
}