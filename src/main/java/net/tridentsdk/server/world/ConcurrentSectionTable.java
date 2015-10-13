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

import net.tridentsdk.meta.nbt.NBTEncoder;
import net.tridentsdk.meta.nbt.NBTException;
import net.tridentsdk.meta.nbt.NBTSerializer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Allows for higher throughput chunk memory I/O for concurrent access
 *
 * @author The TridentSDK Team
 */
public class ConcurrentSectionTable {
    private final ChunkSection[] sections = new ChunkSection[16];
    private final Lock[] locks = new Lock[16];

    public ConcurrentSectionTable() {
        for (int i = 0; i < 16; i++) {
            sections[i] = new ChunkSection((byte) i);
            locks[i] = new ReentrantLock();
        }
    }

    public void lockFully() {
        for (Lock lock : locks) {
            lock.lock();
        }
    }

    public void release() {
        for (Lock lock : locks) {
            lock.unlock();
        }
    }

    public ChunkSection get(int i) {
        return sections[i];
    }

    public void set(int i, ChunkSection section) {
        sections[i] = section;
    }

    public void modify(int i, Consumer<ChunkSection> consumer) {
        Lock lock = locks[i];
        lock.lock();
        try {
            consumer.accept(sections[i]);
        } finally {
            lock.unlock();
        }
    }

    public <T> T modify(int i, Function<ChunkSection, T> function) {
        Lock lock = locks[i];
        lock.lock();
        try {
            return function.apply(sections[i]);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        lockFully();
        try {
            for (ChunkSection section : sections) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                try {
                    new NBTEncoder(new DataOutputStream(stream)).encode(NBTSerializer.serialize(section));
                } catch (NBTException e) {
                    e.printStackTrace();
                }
                byte[] bytes = stream.toByteArray();
                builder.append(bytes[0]).append("-").append(bytes[1]).append("-").append(bytes[bytes.length / 2]).append("-").append(bytes[bytes.length - 2]).append("-").append(bytes[bytes.length - 1]).append(" [").append("s/").append(bytes.length).append(" h/").append(section.y).append("]==> ");
            }
        } finally {
            release();
        }

        return builder.toString();
    }
}
