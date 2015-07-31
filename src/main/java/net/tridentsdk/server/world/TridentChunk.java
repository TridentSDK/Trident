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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.tridentsdk.base.Block;
import net.tridentsdk.base.Position;
import net.tridentsdk.base.Substance;
import net.tridentsdk.concurrent.SelectableThread;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.meta.nbt.*;
import net.tridentsdk.server.concurrent.ThreadsHandler;
import net.tridentsdk.server.packets.play.out.PacketPlayOutChunkData;
import net.tridentsdk.util.NibbleArray;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.world.Chunk;
import net.tridentsdk.world.ChunkLocation;
import net.tridentsdk.world.ChunkSnapshot;
import net.tridentsdk.world.gen.AbstractGenerator;
import net.tridentsdk.world.gen.AbstractOverlayBrush;
import net.tridentsdk.world.gen.GeneratorRandom;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.Stream;

public class TridentChunk implements Chunk {
    private final TridentWorld world;
    private final ChunkLocation location;
    final SelectableThread executor = ThreadsHandler.chunkExecutor().selectCore();
    private final Set<Entity> entities = Sets.newConcurrentHashSet();

    public ChunkSection[] sections;
    private final AtomicReferenceArray<Integer> heights = new AtomicReferenceArray<>(256);

    private volatile int lastFileAccess;
    private volatile long lastModified;
    private volatile long inhabitedTime;
    private volatile byte lightPopulated;
    private volatile byte terrainPopulated;

    protected TridentChunk(TridentWorld world, int x, int z) {
        this(world, ChunkLocation.create(x, z));
    }

    protected TridentChunk(TridentWorld world, ChunkLocation coord) {
        this.world = world;
        this.location = coord;
        this.lastFileAccess = 0;
        sections = new ChunkSection[16];
        /*for (int i = 0; i < 16; i ++) {
            sections[i] = new ChunkSection();
        }*/
    }

    // TODO decide necessity, also TBD disk storage
    // IMPORTANT: MUST BE CALLED FROM executor
    private ChunkSection[] mapSections() {
        return sections;
    }

    protected int lastFileAccess() {
        return this.lastFileAccess;
    }

    protected void setLastFileAccess(int last) {
        this.lastFileAccess = last;
    }

    @Override
    public Set<Entity> entities() {
        return ImmutableSet.copyOf(entities);
    }

    public Set<Entity> entitiesInternal() {
        return entities;
    }

    @Override
    public void generate() {
        executor.execute(() -> {
            // Don't call mapSections, as this generates them
            ChunkSection[] sections = this.sections;

            for (int i = 0; i < 16; i++) {
                if (sections[i] == null) {
                    sections[i] = new ChunkSection((byte) i);
                }
            }

            // TODO add flag to prevent double generation
            AbstractGenerator generator = world.loader().generator();
            int i = 0;

            for (char[] blockData : generator.generateChunkBlocks(location, heights)) {
                sections[i].setBlocks(blockData);
                i++;
            }

            i = 0;

            for (byte[] dataValues : generator.generateBlockData(location)) {
                sections[i].setData(dataValues);
                i++;
            }

            for (ChunkSection section : sections) {
                if (section.blockLight == null) {
                    section.blockLight = new byte[ChunkSection.LENGTH / 2];
                }

                if (section.skyLight == null) {
                    section.skyLight = new byte[ChunkSection.LENGTH / 2];
                }

                if (section.types == null) {
                    section.types = new char[ChunkSection.LENGTH];
                }
            }

            // DEBUG ===== Makes the entire chunk full brightness, not exactly ideal
            for (i = 0; i < 16; i++) {
                Arrays.fill(sections[i].skyLight, (byte) 255);
            }
            // =====

            //TODO lighting
        });
    }

    public void paint() {
        List<AbstractOverlayBrush> brushes = world.loader().brushes();
        GeneratorRandom random = new GeneratorRandom(world.loader().generator().seed());
        AbstractOverlayBrush.ChunkManipulator manipulator = new AbstractOverlayBrush.ChunkManipulator() {
            @Override
            public void manipulate(int relX, int y, int relZ, Substance substance, byte data) {
                if (relX >= 0 && relX <= 15 && relZ >= 0 && relZ <= 15) {
                    setAt(relX, y, relZ, substance, data, (byte) 255, (byte) 15);
                    return;
                }

                int cx = location.x();
                int cz = location.z();

                int xMinDiff = Math.max(relX, 0) - Math.min(relX, 0);
                int xMaxDiff = Math.max(relX, 15) - Math.min(relX, 15);
                int zMinDiff = Math.max(relZ, 0) - Math.min(relZ, 0);
                int zMaxDiff = Math.max(relZ, 15) - Math.min(relZ, 15);

                if (relX > 15 && relZ > 15) { // q1 +,+
                    ChunkLocation loc = ChunkLocation.create(cx + ceil(xMaxDiff / 16), cz + ceil(zMaxDiff / 16));
                    TridentChunk chunk = world.chunkAt(loc, true);
                    chunk.setAt(relX + xMaxDiff, y, relZ + zMaxDiff, substance, data, (byte) 255, (byte) 15);
                } else if (relX > 15 && relZ < 0) { // q4 +,-
                    ChunkLocation loc = ChunkLocation.create(cx + ceil(xMaxDiff / 16), cz - ceil(zMinDiff / 16));
                    TridentChunk chunk = world.chunkAt(loc, true);
                    chunk.setAt(relX + xMaxDiff, y, relZ - zMinDiff, substance, data, (byte) 255, (byte) 15);
                } else if (relX < 0 && relZ > 15) { // q2 -,+
                    ChunkLocation loc = ChunkLocation.create(cx - ceil(xMinDiff / 16), cz + ceil(zMaxDiff / 16));
                    TridentChunk chunk = world.chunkAt(loc, true);
                    chunk.setAt(relX - xMinDiff, y, relZ + zMaxDiff, substance, data, (byte) 255, (byte) 15);
                } else if (relX < 0 && relZ < 15) { // q3 -,-
                    ChunkLocation loc = ChunkLocation.create(cx - ceil(xMinDiff / 16), cz - ceil(zMinDiff / 16));
                    TridentChunk chunk = world.chunkAt(loc, true);
                    chunk.setAt(relX + xMinDiff, y, relZ - zMinDiff, substance, data, (byte) 255, (byte) 15);
                }
            }

            @Override
            public Block blockAt(int relX, int y, int relZ) {
                if (relX >= 0 && relX <= 15 && relZ >= 0 && relZ <= 15) {
                    return TridentChunk.this.blockAt(relX, y, relZ);
                }

                int cx = location.x();
                int cz = location.z();

                int xMinDiff = Math.max(relX, 0) - Math.min(relX, 0);
                int xMaxDiff = Math.max(relX, 15) - Math.min(relX, 15);
                int zMinDiff = Math.max(relZ, 0) - Math.min(relZ, 0);
                int zMaxDiff = Math.max(relZ, 15) - Math.min(relZ, 15);

                if (relX > 15 && relZ > 15) { // q1 +,+
                    ChunkLocation loc = ChunkLocation.create(cx + ceil(xMaxDiff / 16), cz + ceil(zMaxDiff / 16));
                    TridentChunk chunk = world.chunkAt(loc, true);
                    chunk.blockAt(relX + xMaxDiff, y, relZ + zMaxDiff);
                } else if (relX > 15 && relZ < 0) { // q4 +,-
                    ChunkLocation loc = ChunkLocation.create(cx + ceil(xMaxDiff / 16), cz - ceil(zMinDiff / 16));
                    TridentChunk chunk = world.chunkAt(loc, true);
                    return chunk.blockAt(relX + xMaxDiff, y, relZ - zMinDiff);
                } else if (relX < 0 && relZ > 15) { // q2 -,+
                    ChunkLocation loc = ChunkLocation.create(cx - ceil(xMinDiff / 16), cz + ceil(zMaxDiff / 16));
                    TridentChunk chunk = world.chunkAt(loc, true);
                    return chunk.blockAt(relX - xMinDiff, y, relZ + zMaxDiff);
                } else if (relX < 0 && relZ < 15) { // q3 -,-
                    ChunkLocation loc = ChunkLocation.create(cx - ceil(xMinDiff / 16), cz - ceil(zMinDiff / 16));
                    TridentChunk chunk = world.chunkAt(loc, true);
                    return chunk.blockAt(relX + xMinDiff, y, relZ - zMinDiff);
                }

                return null;
            }
        };

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (AbstractOverlayBrush brush : brushes) {
                    final int finalI = i;
                    final int finalJ = j;
                    executor.execute(() ->
                            brush.brush(location, finalI, maxHeightAt(finalI, finalJ), finalJ, random, manipulator));
                }
            }
        }
    }

    private int ceil(double d) {
        return (int) Math.ceil(d);
    }

    public int maxHeightAt(int x, int z) {
        return heights.get(WorldUtils.heightIndex(x, z));
    }

    @Override
    public int x() {
        return location.x();
    }

    @Override
    public int z() {
        return location.z();
    }

    @Override
    public ChunkLocation location() {
        return this.location;
    }

    @Override
    public TridentWorld world() {
        return this.world;
    }

    @Override
    public Block blockAt(final int relX, final int y, final int relZ) {
        final int index = WorldUtils.blockArrayIndex(relX, y & 15, relZ);

        try {
            return executor.submitTask(() -> {
                ChunkSection[] sections = mapSections();

                int sectionIndex = WorldUtils.section(y);
                ChunkSection section = sections[sectionIndex];

                /* Get block data; use extras accordingly */
                byte b = (byte) (section.types[index] >> 4);
                byte meta = (byte) (section.types[index] & 0xF);

                Substance material = Substance.fromId(b);

                if (material == null) {
                    material = Substance.AIR; // check if valid
                }

                return new TridentBlock(Position.create(world, relX + x() * 16, y, relZ + z() * 16),
                        material, meta);
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            TridentLogger.error(e);
            return null;
        }
    }

    @Override
    public ChunkSnapshot snapshot() {
        final List<CompoundTag> sections = Lists.newArrayList();

        final ChunkSection[][] sections1 = new ChunkSection[1][1];
        executor.execute(() -> {
            sections1[0] = mapSections();

            for (ChunkSection section : sections1[0]) {
                sections.add(NBTSerializer.serialize(section));
            }
        });

        executor.execute(() -> Stream.of(sections1[0]).forEach((s) -> sections.add(NBTSerializer.serialize(s))));

        return new TridentChunkSnapshot(world, location, sections, lastFileAccess, lastModified, inhabitedTime,
                lightPopulated, terrainPopulated);
    }

    public PacketPlayOutChunkData asPacket() {
        try {
            return executor.submitTask(() -> {
                ChunkSection[] sections = mapSections();

                int bitmask = (1 << sections.length) - 1;
                ByteArrayOutputStream data = new ByteArrayOutputStream();

                for (ChunkSection section : sections) {
                    if (section == null)
                        continue;

                    for (char c : section.types()) {
                        data.write(c & 0xff);
                        data.write(c >> 8);
                    }
                }

                for (ChunkSection section : sections) {
                    try {
                        if (section == null) {
                            data.write(0);
                            continue;
                        }

                        data.write(section.blockLight);
                    } catch (IOException e) {
                        TridentLogger.error(e);
                    }
                }

                for (ChunkSection section : sections) {
                    try {
                        if (section == null) {
                            data.write(0);
                            continue;
                        }

                        data.write(section.skyLight);
                    } catch (IOException e) {
                        TridentLogger.error(e);
                    }
                }

                for (int i = 0; i < 256; i += 1) {
                    data.write(0);
                }

                return new PacketPlayOutChunkData(data.toByteArray(), location, false, (short) bitmask);
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            TridentLogger.error(e);
            return null;
        }
    }

    public void load(CompoundTag root) {
        CompoundTag tag = root.getTagAs("Level");
        LongTag lastModifed = tag.getTagAs("LastUpdate");
        ByteTag lightPopulated = (tag.containsTag("LightPopulated")) ? (ByteTag) tag.getTagAs(
                "LightPopulated") : new ByteTag("LightPopulated").setValue((byte) 0);
        ByteTag terrainPopulated = tag.getTagAs("TerrainPopulated");

        LongTag inhabitedTime = tag.getTagAs("InhabitedTime");
        IntArrayTag biomes = tag.getTagAs("HeightMap");

        final ListTag sectionTags = tag.getTagAs("Sections");
        ListTag entities = tag.getTagAs("Entities");
        ListTag tileEntities = (tag.containsTag("TileEntities")) ? (ListTag) tag.getTag("TileEntities") :
                new ListTag("TileEntities", TagType.COMPOUND);
        ListTag tileTicks = (tag.containsTag("TileTicks")) ? (ListTag) tag.getTag("TileTicks") : new ListTag(
                "TileTicks", TagType.COMPOUND);
        final List<NBTTag> sectionsList = sectionTags.listTags();

        final ChunkSection[] sections = new ChunkSection[sectionsList.size()];

                /* Load sections */
        for (int i = 0; i < sectionsList.size(); i += 1) {
            NBTTag t = sectionTags.getTag(i);

            if (t instanceof CompoundTag) {
                CompoundTag ct = (CompoundTag) t;

                ChunkSection section = NBTSerializer.deserialize(ChunkSection.class, ct);

                section.loadBlocks();
                sections[section.y()] = section;
            }
        }

        executor.execute(() -> this.sections = sections);

        for (NBTTag t : entities.listTags()) {
            //TridentEntity entity = EntityBuilder.create().build(TridentEntity.class);

            //entity.load((CompoundTag) t);
            //world.entities().add(entity);
        }

        /* Load extras */
        this.lightPopulated = lightPopulated.value(); // Unknown use
        this.terrainPopulated = terrainPopulated.value(); // if chunk was populated with special things (ores,
        // trees, etc.), if 1 regenerate
        this.lastModified = lastModifed.value(); // Tick when the chunk was last saved
        this.inhabitedTime = inhabitedTime.value(); // Cumulative number of ticks player have been in the chunk
    }

    @Override
    public void unload() {
        world.loader().saveChunk(this);
        world.loadedChunks.remove(location);
    }

    public CompoundTag asNbt() {
        CompoundTag root = new CompoundTag("root");
        CompoundTag level = new CompoundTag("Level");

        level.addTag(new LongTag("LastUpdate").setValue(world.time()));
        level.addTag(new ByteTag("LightPopulated").setValue(lightPopulated));
        level.addTag(new ByteTag("TerrainPopulated").setValue(terrainPopulated));

        level.addTag(new LongTag("InhabitedTime").setValue(inhabitedTime));
        level.addTag(new IntArrayTag("HeightMap").setValue(new int[1024])); // placeholder TODO

        final ListTag sectionTags = new ListTag("Sections", TagType.COMPOUND);

        ChunkSection[] sectionCopy = new ChunkSection[0];
        if (Thread.currentThread().equals(executor.asThread())) {
            sectionCopy = mapSections();
        } else {
            try {
                sectionCopy = executor.submitTask(this::mapSections).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        for (ChunkSection section : sectionCopy) {
            section.updateRaw();
            sectionTags.addTag(NBTSerializer.serialize(section));
        }

        level.addTag(sectionTags);
        level.addTag(new ListTag("Entities", TagType.COMPOUND)); // another placeholder TODO

        root.addTag(level);

        return root;
    }

    public void setAt(Position p, Substance type, byte metaData, byte skyLight, byte blockLight) {
        setAt((int) p.x(), (int) p.y(), (int) p.z(), type, metaData, skyLight, blockLight);
    }

    public void setAt(int x, final int y, int z, final Substance type, final byte metaData, final byte skyLight,
                      final byte blockLight) {
        final int index = WorldUtils.blockArrayIndex(x & 15, y & 15, z & 15);
        executor.execute(() -> {
            ChunkSection[] sections = mapSections();

            ChunkSection section = sections[WorldUtils.section(y)];

            section.types[index] = (char) ((type.asExtended() & 0xfff0) | metaData);
            NibbleArray.set(section.data, index, metaData);
            NibbleArray.set(section.skyLight, index, skyLight);
            NibbleArray.set(section.blockLight, index, blockLight);
        });
    }
}