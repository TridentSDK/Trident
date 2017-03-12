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
package net.tridentsdk.server.world;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.base.Block;
import net.tridentsdk.base.ImmutableWorldVector;
import net.tridentsdk.server.concurrent.PoolSpec;
import net.tridentsdk.server.concurrent.ServerThreadPool;
import net.tridentsdk.server.util.UncheckedCdl;
import net.tridentsdk.server.world.gen.GeneratorContextImpl;
import net.tridentsdk.world.Chunk;
import net.tridentsdk.world.World;
import net.tridentsdk.world.gen.*;
import net.tridentsdk.world.opt.Dimension;
import net.tridentsdk.world.opt.GenOpts;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

import static net.tridentsdk.server.net.NetData.wvint;

/**
 * Represents a chunk column.
 */
public class TridentChunk implements Chunk {
    /**
     * Thread pool used for arbitrary container generation
     */
    public static final ServerThreadPool ARBITRARY_POOL = ServerThreadPool.forSpec(PoolSpec.CHUNKS);
    /**
     * Thread pool used for default container generation
     */
    public static final ServerThreadPool DEFAULT_POOL = ServerThreadPool.forSpec(PoolSpec.PLUGINS);

    /**
     * The ready getState for this chunk, whether it has fully
     * generated yet.
     */
    private final UncheckedCdl ready = new UncheckedCdl(1);
    /**
     * The world in which this chunk is located
     */
    private final TridentWorld world;
    /**
     * The x coordinate
     */
    private final int x;
    /**
     * The z coordinate
     */
    private final int z;

    /**
     * An empty ChunkSection which is used in place of a
     * null element to save memory. This must be set in
     * the constructor in order to account for differences
     * in chunk format for different dimensions.
     */
    private final ChunkSection emptyPlaceholder;
    /**
     * The sections that the chunk has generated
     */
    private final AtomicReferenceArray<ChunkSection> sections = new AtomicReferenceArray<>(16);
    /**
     * The height map for this chunk, 16x16 indexed by x
     * across then adding z (x << 4 | z & 0xF)
     */
    private final AtomicIntegerArray heights = new AtomicIntegerArray(256);

    /**
     * Creates a new chunk at the specified coordinates.
     *
     * @param world the world which contains this chunk
     * @param x the x coordinate
     * @param z the z coordinate
     */
    public TridentChunk(TridentWorld world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;

        if (world.getWorldOptions().getDimension() == Dimension.OVERWORLD) {
            this.emptyPlaceholder = ChunkSection.EMPTY_WITH_SKYLIGHT;
        } else {
            this.emptyPlaceholder = ChunkSection.EMPTY_WITHOUT_SKYLIGHT;
        }
    }

    /**
     * Generates the chunk.
     */
    public void generate() {
        GenOpts opts = this.world.getGeneratorOptions();
        GeneratorProvider provider = opts.getProvider();

        Executor container = provider.getGenerationContainer();
        if (container == GenContainer.DEFAULT) {
            container = DEFAULT_POOL;
        } else if (container == GenContainer.ARBITRARY) {
            container = ARBITRARY_POOL;
        }

        TerrainGenerator terrain = provider.getTerrainGenerator(this.world);
        Set<PropGenerator> props = provider.getPropGenerators(this.world);
        Set<FeatureGenerator> features = provider.getFeatureGenerators(this.world);
        GeneratorContextImpl context = new GeneratorContextImpl(container, opts.getSeed(),
                this.world.getWorldOptions().getDimension() == Dimension.OVERWORLD);

        CompletableFuture.supplyAsync(() -> {
            terrain.generate(this.x, this.z, context);
            for (FeatureGenerator generator : features) {
                generator.generate(this.x, this.z, context);
            }

            UncheckedCdl latch = context.getCount();
            context.doRun(latch);
            return latch;
        }, container).thenApplyAsync(l -> {
            l.await();
            context.reset();

            for (PropGenerator generator : props) {
                generator.generate(this.x, this.z, context);
            }

            UncheckedCdl latch = context.getCount();
            context.doRun(latch);

            return latch;
        }, container).thenAcceptAsync(l -> {
            l.await();
            context.copySections(this.sections);
            context.copyHeights(this.heights);

            this.ready.countDown();
        }, container);

        this.waitReady();
    }

    /**
     * Awaits for the chunk ready getState to finish,
     * indicating that the chunk has finished generation.
     *
     * @return the chunk, when ready
     */
    public TridentChunk waitReady() {
        this.ready.await();
        return this;
    }

    /**
     * Write the chunk data to the given buffer for sending
     * to players via the protocol.
     *
     * @param buf the buffer to write the chunk data
     * @param continuous {@code true} if the entire chunk
     * is sent bottom to top
     */
    public void write(ByteBuf buf, boolean continuous) {
        int len = this.sections.length();

        // Copy chunk sections to local array in order to
        // prevent upddates from breaking the packet
        ChunkSection[] sections = new ChunkSection[16];
        for (int i = 0; i < 16; i++) {
            sections[i] = this.sections.get(i);
        }

        // Write the continuous mask
        short mask = 0;
        for (int i = 0; i < len; i++) {
            if (sections[i] != null) {
                mask |= 1 << i;
            }
        }
        wvint(buf, mask);

        // Write section data
        ByteBuf chunkData = buf.alloc().buffer();
        for (int i = 0; i < len; i++) {
            if ((mask & (1 << i)) == (1 << i)) {
                ChunkSection sec = sections[i];
                if (sec != null) {
                    sec.write(chunkData);
                } else {
                    this.emptyPlaceholder.write(chunkData);
                }
            }
        }
        wvint(buf, chunkData.readableBytes() + (continuous ? 256 : 0));
        buf.writeBytes(chunkData);
        chunkData.release();

        // If continous, write the biome data
        if (continuous) {
            for (int i = 0; i < 256; i++) {
                buf.writeByte(1);
            }
        }

        // TODO - Tile entities
        wvint(buf, 0);
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getZ() {
        return this.z;
    }

    @Nonnull
    @Override
    public Block getBlockAt(int x, int y, int z) {
        return new TridentBlock(new ImmutableWorldVector(this.world, this.x << 4 + x, y, this.z << 4 + z));
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    /**
     * Obtains the stored block data at the given relative
     * coordinates in the chunk.
     *
     * @param x the relative x
     * @param y the relative y
     * @param z the relative z
     * @return the block data, which incorporates the
     * substance ID and the block meta
     */
    public short get(int x, int y, int z) {
        ChunkSection section = this.sections.get(y >> 4);
        if (section == null) {
            return 0;
        }

        return section.dataAt(y << 8 | z << 4 | x);
    }

    /**
     * Set a block state inside the chunk
     *
     * @param x Relative X position of the block inside the chunk
     * @param y Relative Y position of the block inside the chunk
     * @param z Relative Z position of the block inside the chunk
     * @param state The state of the block
     */
    public void set(int x, int y, int z, short state) {
        int sectionIdx = y >> 4;

        ChunkSection section = this.sections.get(sectionIdx);
        if (section == null) {
            ChunkSection newSec = new ChunkSection(this.world.getWorldOptions().getDimension() == Dimension.OVERWORLD);
            if (this.sections.compareAndSet(sectionIdx, null, newSec)) {
                section = newSec;
            } else {
                section = this.sections.get(sectionIdx);
            }
        }

        section.set(y << 8 | z << 4 | x, state);
    }
}
