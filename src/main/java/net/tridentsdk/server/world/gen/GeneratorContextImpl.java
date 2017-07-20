/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
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
package net.tridentsdk.server.world.gen;

import net.tridentsdk.base.Substance;
import net.tridentsdk.server.util.UncheckedCdl;
import net.tridentsdk.server.world.ChunkSection;
import net.tridentsdk.world.gen.GeneratorContext;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;

/**
 * Implementation of a generator context.
 */
@ThreadSafe
public class GeneratorContextImpl implements GeneratorContext {
    /**
     * The container for running generator tasks in this
     * context
     */
    private final Executor container;
    /**
     * The count of threads active for termination used
     * for termination signalling
     */
    private final LongAdder count = new LongAdder();
    /**
     * Queue of generation tasks to be handle upon command
     */
    private final Queue<Consumer<UncheckedCdl>> tasks = new ConcurrentLinkedQueue<>();

    /**
     * The seed to be used for generation
     */
    private final long seed;
    /**
     * Whether or not section skylight is written for the
     * chunk to be generated
     */
    private final boolean doSkylight;
    /**
     * The last random value, used for the PRNG generator
     */
    private final AtomicLong random;


    /**
     * List of chunk sections
     */
    private final AtomicReferenceArray<ChunkSection> sections = new AtomicReferenceArray<>(16);
    /**
     * Mapping of highest Y
     */
    private final AtomicIntegerArray maxY = new AtomicIntegerArray(256);

    /**
     * Creates a new generator context with the given seed
     * as the starting random.
     *
     * @param container where to run generator tasks
     * @param seed the seed
     * @param doSkylight whether to generate skylight
     */
    public GeneratorContextImpl(Executor container, long seed, boolean doSkylight) {
        this.container = container;
        this.seed = seed;
        this.doSkylight = doSkylight;

        this.random = new AtomicLong(seed);
    }

    @Override
    public long nextLong() {
        while (true) {
            long l = this.random.get();

            long x = l;
            x ^= (x << 21);
            x ^= (x >>> 35);
            x ^= (x << 4);

            if (x != 0 && this.random.compareAndSet(l, x)) {
                return x;
            }
        }
    }

    @Override
    public long nextLong(long max) {
        return this.nextLong() % max;
    }

    @Override
    public int nextInt() {
        return this.nextInt(Integer.MAX_VALUE);
    }

    @Override
    public int nextInt(int max) {
        return (int) this.nextLong() % max;
    }

    @Override
    public long seed() {
        return this.seed;
    }

    @Override
    public int maxHeight(int x, int z) {
        return this.maxY.get(x << 4 | z & 0xF);
    }

    @Override
    public void set(int x, int y, int z, Substance substance, byte meta) {
        this.set(x, y, z, build(substance.getId(), meta));
    }

    @Override
    public void set(int x, int y, int z, Substance substance) {
        this.set(x, y, z, build(substance.getId(), (byte) 0));
    }

    @Override
    public void set(int x, int y, int z, int id, byte meta) {
        this.set(x, y, z, build(id, meta));
    }

    @Override
    public void run(Runnable r) {
        this.count.increment();
        this.tasks.offer((cdl) -> {
            r.run();
            cdl.countDown();
        });
    }

    /**
     * Sends the command for the container to handle the tasks
     * that were scheduled by the terrain generator.
     *
     * @param latch the count down latch used to await for
     * the generation to finish before
     * proceeding
     */
    public void doRun(UncheckedCdl latch) {
        for (Consumer<UncheckedCdl> consumer : this.tasks) {
            this.container.execute(() -> consumer.accept(latch));
        }
    }

    /**
     * Obtains the latch in order to determine the amount
     * of
     * runs necessary to complete all of the scheduled
     * generation tasks.
     *
     * @return the count down latch argument
     */
    public UncheckedCdl getCount() {
        return new UncheckedCdl(this.count.intValue());
    }

    /**
     * Resets the task runner and the available tasks left
     * counter in order to reuse the same context for prop
     * generators.
     */
    public void reset() {
        this.count.reset();
        this.tasks.clear();
    }

    /**
     * Obtains the collection of chunk sections that were
     * generated by the context as an array.
     *
     * @param sections the array of chunk sections which to
     * copy the generated
     */
    public void copySections(AtomicReferenceArray<ChunkSection> sections) {
        for (int i = 0; i < this.sections.length(); i++) {
            sections.set(i, this.sections.get(i));
        }
    }

    /**
     * Copies the height map to the given array.
     *
     * <p>I've decided to copy here in order to remove an
     * extra volatile that would have been necessary if we
     * had actually set the array for generation to the
     * chunk array.</p>
     *
     * @param array the array to copy to
     */
    public void copyHeights(AtomicIntegerArray array) {
        for (int i = 0; i < array.length(); i++) {
            array.set(i, this.maxY.get(i));
        }
    }

    /**
     * Sets the block at the given coordinates to the given
     * block getState value.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param state the block to set
     */
    private void set(int x, int y, int z, short state) {
        int sectionIdx = section(y);
        int idx = idx(x, y & 15, z);
        int xz = x << 4 | z & 0xF;

        ChunkSection section = this.sections.get(sectionIdx);
        if (section == null) {
            ChunkSection newSec = new ChunkSection(this.doSkylight);
            // if we end up with no chunk section
            // try to cas null -> newsec
            if (this.sections.compareAndSet(sectionIdx, null, newSec)) {
                // if we win the race, we use the same sec
                section = newSec;
            } else {
                // if we lose the race, retry
                section = this.sections.get(sectionIdx);
            }
        }

        int lastMax;
        do {
            lastMax = this.maxY.get(xz);
            if (y <= lastMax) {
                break;
            }
        } while (!this.maxY.compareAndSet(xz, lastMax, y));

        section.set(idx, state);
    }

    /**
     * Builds the given block getState given the ID number and
     * the metadata value.
     *
     * @param id the block ID
     * @param meta the block meta
     * @return the block getState
     */
    // short is perfect for storing block data because
    // short = 2 bytes = 16 bits
    // 8 bit block id
    // 4 bit meta
    // 4 bit add (unused)
    // ------------------
    // 16 bits
    private static short build(int id, byte meta) {
        return (short) (id << 4 | meta);
    }

    /**
     * http://minecraft.gamepedia.com/Chunk_format
     * int BlockPos = y*16*16 + z*16 + x;
     *
     * return (y * (2^8)) + (z * (2^4)) + x;
     * use OR instead because bitwise ops are faster and
     * provides the same results as addition
     *
     * max size of this array is blocks in section, 4096
     * 16*16*16
     */
    private static int idx(int x, int y, int z) {
        return y << 8 | z << 4 | x;
    }

    /**
     * Obtains the section number for the given Y value.
     *
     * @param y the y value
     * @return the section number for that Y value
     */
    private static int section(int y) {
        return y >> 4;
    }
}
