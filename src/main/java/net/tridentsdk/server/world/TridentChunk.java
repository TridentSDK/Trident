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
import lombok.Getter;
import net.tridentsdk.base.Block;
import net.tridentsdk.base.Position;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.meta.nbt.Tag;
import net.tridentsdk.server.concurrent.PoolSpec;
import net.tridentsdk.server.concurrent.ServerThreadPool;
import net.tridentsdk.server.entity.TridentEntity;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.util.UncheckedCdl;
import net.tridentsdk.server.world.gen.GeneratorContextImpl;
import net.tridentsdk.world.Chunk;
import net.tridentsdk.world.gen.*;
import net.tridentsdk.world.opt.Dimension;
import net.tridentsdk.world.opt.GenOpts;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Stream;

import static net.tridentsdk.server.net.NetData.wvint;

/**
 * Represents a chunk column.
 */
@ThreadSafe
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
    @Getter
    private final TridentWorld world;
    /**
     * The x coordinate
     */
    @Getter
    private final int x;
    /**
     * The z coordinate
     */
    @Getter
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
     * The number of ticks that all players have spent in
     * this chunk
     */
    private final LongAdder inhabited = new LongAdder();

    /**
     * The players that currently occupy this chunk
     */
    @Getter
    private final Set<TridentPlayer> occupants = Collections.newSetFromMap(new ConcurrentHashMap<>());
    /**
     * The players that have this chunk loaded
     */
    @Getter
    private final Set<TridentPlayer> holders = Collections.newSetFromMap(new ConcurrentHashMap<>());
    /**
     * The entities in this chunk
     */
    @Getter
    private final Set<TridentEntity> entitySet = Collections.newSetFromMap(new ConcurrentHashMap<>());

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
     * Ticks the chunk, updating the inhabited time, tile
     * entities, stateful blocks, and entities.
     */
    public void tick() {
        this.inhabited.add(this.occupants.size());
    }

    /**
     * Generates the chunk.
     */
    public void generate() {
        if (this.ready.getCount() == 0) {
            return;
        }

        Region region = Region.getFile(this, false);
        if (region == null) {
            this.runGenerator();
        } else {
            int rX = this.x & 31;
            int rZ = this.z & 31;
            if (region.hasChunk(rX, rZ)) {
                try (DataInputStream in = region.getChunkDataInputStream(rX, rZ)) {
                    Tag.Compound compound = Tag.decode(in).getCompound("Level");
                    CompletableFuture.runAsync(() -> this.read(compound), ARBITRARY_POOL).
                            whenCompleteAsync((v, t) -> {
                                if (this.ready.getCount() == 1) {
                                    this.runGenerator();
                                }
                            }, ARBITRARY_POOL);
                    this.waitReady();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                this.runGenerator();
            }
        }
    }

    /**
     * Runs the custom generator function when the data is
     * not loaded into memory, or if the chunk has no data
     * to load.
     */
    private void runGenerator() {
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
        // prevent updates from breaking the packet
        short mask = 0;
        ChunkSection[] sections = new ChunkSection[16];
        for (int i = 0; i < 16; i++) {
            ChunkSection sec = this.sections.get(i);
            sections[i] = sec;

            if (sec != null) {
                mask |= 1 << i;
            }
        }

        // Write the continuous mask
        wvint(buf, mask);

        // Write section data
        ByteBuf chunkData = buf.alloc().buffer();
        try {
            for (int i = 0; i < len; i++) {
                if ((mask & 1 << i) == 1 << i) {
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
        } finally {
            chunkData.release();
        }

        // If continuous, write the biome data
        if (continuous) {
            for (int i = 0; i < 256; i++) {
                buf.writeByte(1);
            }
        }

        // TODO - Tile entities
        wvint(buf, 0);
    }

    @Nonnull
    @Override
    public Block getBlockAt(int x, int y, int z) {
        return new TridentBlock(new Position(this.world, this.x << 4 + x, y, this.z << 4 + z));
    }

    @Override
    public Set<? extends Player> getPlayers() {
        return Collections.unmodifiableSet(this.occupants);
    }

    @Override
    public Stream<? extends Entity> getEntities() {
        return Stream.concat(this.occupants.stream(), this.entitySet.stream());
    }

    /**
     * Obtains the highest Y value at the given chunk
     * relative X/Z coordinates.
     *
     * @param x the relative X
     * @param z the relative Z
     * @return the highest Y value
     */
    public int getHighestY(int x, int z) {
        return this.heights.get(x << 4 | z & 0xF);
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

        return section.dataAt((y & 15) << 8 | z << 4 | x);
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

        int heightIdx = x << 4 | z & 0xF;
        int height;
        int newHeight;
        do {
            height = this.heights.get(heightIdx);
            newHeight = height;

            if (y > height) {
                newHeight = height;
            } else {
                for (int i = height; i >= 0; i--) {
                    if (this.get(x, i, z) >> 4 != 0) {
                        newHeight = i;
                        break;
                    }
                }
            }
        } while (!this.heights.compareAndSet(heightIdx, height, newHeight));

        section.set((y & 15) << 8 | z << 4 | x, state);
    }

    /**
     * Reads the chunk data from the region file compound.
     *
     * @param compound the compound to read
     */
    public void read(Tag.Compound compound) {
        this.inhabited.add(compound.getLong("InhabitedTime"));

        Tag.List<Tag.Compound> sectionList = compound.getList("Sections");
        for (Tag.Compound c : sectionList) {
            ChunkSection section = new ChunkSection(this.world.getWorldOptions().getDimension() == Dimension.OVERWORLD);
            section.read(c);

            byte y = c.getByte("Y");
            this.sections.set(y, section);
        }

        int[] heightMap = compound.getIntArray("HeightMap");
        for (int i = 0; i < heightMap.length; i++) {
            this.heights.set(i, heightMap[i]);
        }

        if (compound.getByte("TerrainPopulated") == 1) {
            this.ready.countDown();
        }
    }

    /**
     * Writes the chunk data to the region file compound.
     *
     * TODO
     * Biomes (byte_array)
     * TileEntities (list [tag compound?])
     * Entities (list [tag compound?])
     *
     * @param compound the compound to write
     */
    public void write(Tag.Compound compound) {
        compound.putInt("xPos", this.x);
        compound.putInt("zPos", this.z);

        byte hasGenerated = (byte) (this.ready.getCount() == 0 ? 1 : 0);
        compound.putByte("TerrainPopulated", hasGenerated);
        compound.putByte("LightPopulated", hasGenerated);
        compound.putLong("InhabitedTime", this.inhabited.longValue());
        compound.putLong("LastUpdate", this.world.getTime());

        Tag.List<Tag.Compound> sectionList = new Tag.List<>(Tag.Type.COMPOUND);
        for (int i = 0; i < this.sections.length(); i++) {
            ChunkSection section = this.sections.get(i);
            if (section != null) {
                Tag.Compound sectionCompound = new Tag.Compound("");
                sectionCompound.putByte("Y", (byte) i);
                section.write(sectionCompound);
                sectionList.add(sectionCompound);
            }
        }
        compound.putList("Sections", sectionList);

        int[] heightMap = new int[this.heights.length()];
        for (int i = 0; i < heightMap.length; i++) {
            heightMap[i] = this.heights.get(i);
        }
        compound.putIntArray("HeightMap", heightMap);
    }
}