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
package net.tridentsdk.server.world;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.tridentsdk.base.Block;
import net.tridentsdk.server.world.gen.GeneratorContextImpl;
import net.tridentsdk.world.Chunk;
import net.tridentsdk.world.World;
import net.tridentsdk.world.gen.*;
import net.tridentsdk.world.opt.GenOpts;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static net.tridentsdk.server.net.NetData.wvint;

/**
 * Represents a chunk column.
 */
public class TridentChunk implements Chunk {
    /**
     * An empty chunk section, used for ground up
     * continuous
     */
    private static final ChunkSection EMPTY_SECTION = new ChunkSection();

    /**
     * The ready state for this chunk, whether it has fully
     * generated yet.
     */
    private final CountDownLatch ready = new CountDownLatch(1);
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
     * The sections that the chunk has generated
     */
    private volatile ChunkSection[] sections;

    /**
     * Creates a new chunk at the specified coordinates.
     *
     * @param x the x coordinate
     * @param z the z coordinate
     */
    public TridentChunk(TridentWorld world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    /**
     * Generates the chunk.
     */
    public void generate() {
        // TODO container + other generators
        GenOpts opts = this.world.genOpts();
        GeneratorProvider provider = opts.provider();
        GenContainer container = provider.container();

        TerrainGenerator terrain = provider.terrain(this.world);
        Set<PropGenerator> props = provider.propSet(this.world);
        Set<FeatureGenerator> features = provider.featureSet(this.world);

        GeneratorContextImpl context = new GeneratorContextImpl(opts.seed());
        terrain.generate(this.x, this.z, context);

        this.sections = context.asArray();

        this.ready.countDown();
    }

    /**
     * Awaits for the chunk ready state to finish,
     * indicating that the chunk has finished generation.
     *
     * @return the chunk, when ready
     */
    public TridentChunk waitReady() {
        try {
            this.ready.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    /**
     * Write the chunk data to the given buffer.
     *
     * @param buf the buffer to write the chunk data
     * @param continuous {@code true} if the entire chunk
     * is sent bottom to top
     */
    public void write(ByteBuf buf, boolean continuous) {
        ChunkSection[] sections = this.sections;

        short mask = 0;
        for (int i = 0; i < sections.length; i++) {
            if (sections[i] == null) break;
            mask |= 1 << i;
        }
        wvint(buf, mask);

        ByteBuf chunkData = Unpooled.buffer();
        for (int i = 0; i < sections.length; i++) {
            if ((mask & 1 << i) == 1) {
                if (sections[i] != null) {
                    sections[i].write(chunkData);
                } else {
                    EMPTY_SECTION.write(chunkData);
                }
            }
        }

        wvint(buf, chunkData.readableBytes() + (continuous ? 256 : 0));
        buf.writeBytes(chunkData);
        chunkData.release();

        if (continuous) {
            for (int i = 0; i < 256; i++) {
                buf.writeByte(1);
            }
        }

        wvint(buf, 0);
    }

    @Override
    public int x() {
        return this.x;
    }

    @Override
    public int z() {
        return this.z;
    }

    @Nonnull
    @Override
    public Block blockAt(int x, int y, int z) {
        return null;
    }

    @Override
    public World world() {
        return this.world;
    }
}