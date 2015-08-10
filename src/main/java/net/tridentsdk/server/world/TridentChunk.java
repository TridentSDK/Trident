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
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.tridentsdk.base.Block;
import net.tridentsdk.base.BoundingBox;
import net.tridentsdk.base.Position;
import net.tridentsdk.base.Substance;
import net.tridentsdk.concurrent.Joiner;
import net.tridentsdk.concurrent.SelectableThread;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.meta.block.BlockMeta;
import net.tridentsdk.meta.block.Tile;
import net.tridentsdk.meta.nbt.*;
import net.tridentsdk.server.concurrent.ThreadsHandler;
import net.tridentsdk.server.entity.TridentEntity;
import net.tridentsdk.server.packets.play.out.PacketPlayOutChunkData;
import net.tridentsdk.server.world.change.ThreadSafeChange;
import net.tridentsdk.util.NibbleArray;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.util.Vector;
import net.tridentsdk.world.Chunk;
import net.tridentsdk.world.ChunkLocation;
import net.tridentsdk.world.ChunkSnapshot;
import net.tridentsdk.world.MassChange;
import net.tridentsdk.world.gen.AbstractGenerator;
import net.tridentsdk.world.gen.AbstractOverlayBrush;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TridentChunk implements Chunk {
    private final TridentWorld world;
    private final ChunkLocation location;
    final SelectableThread executor = ThreadsHandler.chunkExecutor().selectCore();

    private final Set<Entity> entities = Sets.newConcurrentHashSet();
    private final Map<Vector, List<BlockMeta>> blockMeta = Maps.newConcurrentMap();

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

    @Override
    public Collection<Tile> tiles() {
        List<Tile> concat = Lists.newArrayList();
        for (List<BlockMeta> metaList : blockMeta.values()) {
            for (BlockMeta meta : metaList) {
                if (meta instanceof Tile) {
                    concat.add((Tile) meta);
                }
            }
        }

        return concat;
    }

    public Set<Entity> entitiesInternal() {
        return entities;
    }

    public Map<Vector, List<BlockMeta>> tilesInternal() {
        return blockMeta;
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

    private void generateSpecial() {
        Joiner joiner = new Joiner();
        executor.execute(() -> {
            // Don't call mapSections, as this generates them
            ChunkSection[] sections = this.sections;

            for (int i = 0; i < 16; i++) {
                if (sections[i] == null) {
                    sections[i] = new ChunkSection((byte) i);
                }
            }

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

            joiner.doJoin();
            //TODO lighting
        });
        joiner.await();
    }

    public void paint() {
        if (terrainPopulated == 0x01) {
            return;
        }

        for (int i = location.x() - 1; i <= location.x() + 1; i++) {
            for (int j = location.z() - 1; j <= location.z() + 1; j++) {
                rawChunk(ChunkLocation.create(i, j));
            }
        }

        List<AbstractOverlayBrush> brushes = world.loader().brushes();
        AbstractOverlayBrush.ChunkManipulator manipulator = new AbstractOverlayBrush.ChunkManipulator() {
            private final MassChange change = new ThreadSafeChange(world);

            @Override
            public void manipulate(int relX, int y, int relZ, Substance substance, byte data) {
                if (relX >= 0 && relX <= 15 && relZ >= 0 && relZ <= 15) {
                    setAndSend(relX, y, relZ, substance, data, (byte) 255, (byte) 15, change);
                    return;
                }

                int cx = location.x();
                int cz = location.z();

                int xMinDiff = Math.max(relX, 0) - Math.min(relX, 0);
                int xMaxDiff = Math.max(relX, 15) - Math.min(relX, 15);
                int zMinDiff = Math.max(relZ, 0) - Math.min(relZ, 0);
                int zMaxDiff = Math.max(relZ, 15) - Math.min(relZ, 15);

                /*      x
                        |
                   -,-  |  -,+
                        |
                ----------------- z (x,z)
                        |
                   +,-  |  +,+
                        |
                 */

                int chunkX = location.x();
                int chunkZ = location.z();
                int newX = relX;
                int newZ = relZ;

                if (relX < 0) {
                    newX = 16 - xMinDiff;
                    chunkX = cx - up(xMinDiff / 16) - 1;
                } else if (relX > 15) {
                    newX = xMaxDiff - 1;
                    chunkX = cx + up(xMaxDiff / 16) + 1;
                }

                if (relZ < 0){
                    newZ = 16 - zMinDiff;
                    chunkZ = cz - up(zMinDiff / 16) - 1;
                } else if (relZ > 15) {
                    newZ = zMaxDiff - 1;
                    chunkZ = cz + up(zMaxDiff / 16) + 1;
                }

                ChunkLocation loc = ChunkLocation.create(chunkX, chunkZ);
                TridentChunk chunk = rawChunk(loc);
                TridentLogger.get().debug(relX + ", " + relZ + " with " + xMinDiff + ", " + xMaxDiff + " / " + zMinDiff + ", " + zMaxDiff + " led to " + newX + ", " + newZ);
                chunk.setAndSend(newX, y, newZ, substance, data, (byte) 255, (byte) 15, change);
            }

            @Override
            public Block blockAt(int relX, int y, int relZ) {
                if (y == Integer.MAX_VALUE) {
                    change.commitChanges();
                    return null;
                }

                if (relX >= 0 && relX <= 15 && relZ >= 0 && relZ <= 15) {
                    return TridentChunk.this.blockAt(relX, y, relZ);
                }

                int cx = location.x();
                int cz = location.z();

                int xMinDiff = Math.max(relX, 0) - Math.min(relX, 0);
                int xMaxDiff = Math.max(relX, 15) - Math.min(relX, 15);
                int zMinDiff = Math.max(relZ, 0) - Math.min(relZ, 0);
                int zMaxDiff = Math.max(relZ, 15) - Math.min(relZ, 15);

                int chunkX = location.x();
                int chunkZ = location.z();
                int newX = relX;
                int newZ = relZ;

                if (relX < 0) {
                    newX = 16 - xMinDiff;
                    chunkX = cx - up(xMinDiff / 16) - 1;
                } else if (relX > 15) {
                    newX = xMaxDiff - 1;
                    chunkX = cx + up(xMaxDiff / 16) + 1;
                }

                if (relZ < 0){
                    newZ = 16 - zMinDiff;
                    chunkZ = cz - up(zMinDiff / 16) - 1;
                } else if (relZ > 15) {
                    newZ = zMaxDiff - 1;
                    chunkZ = cz + up(zMaxDiff / 16) + 1;
                }

                ChunkLocation loc = ChunkLocation.create(chunkX, chunkZ);
                TridentChunk chunk = rawChunk(loc);
                return chunk.blockAt(newX, y, newZ);
            }
        };

        CountDownLatch latch = new CountDownLatch(256);
        for (int i = 0; i < 16; i++) {
            final int finalI = i;
            executor.execute(() -> {
                for (int j = 0; j < 16; j++) {
                    for (AbstractOverlayBrush brush : brushes) {
                        brush.brush(location, finalI, j, world.random(), heights, manipulator);
                        latch.countDown();
                    }
                }
            });
        }
        manipulator.blockAt(0, Integer.MAX_VALUE, 0);

        // Label as populated, so the chunk is not repopulated
        terrainPopulated = 0x01;

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private TridentChunk rawChunk(ChunkLocation location) {
        TridentChunk worldChunk = world.chunkAt(location, false);
        if (worldChunk != null) return worldChunk;

        if (world.loader().chunkExists(location)) {
            Chunk c = world.loader().loadChunk(location);
            if (c != null) {
                world.addChunkAt(location, c);
                return (TridentChunk) c;
            }
        }

        TridentChunk chunk = new TridentChunk(world, location);
        world.addChunkAt(location, chunk);
        chunk.generateSpecial();
        return chunk;
    }

    private int up(double d) {
        if (Math.rint(d) != d)
            return ((int) d) + 1;
        return (int) d;
    }

    private void setAndSend(int x, final int y, int z, final Substance type, final byte metaData, final byte skyLight,
                            final byte blockLight, MassChange change) {
        setAt(x, y, z, type, metaData, skyLight, blockLight);
        change.setBlock(WorldUtils.heightIndex(location.x(), x), y, WorldUtils.heightIndex(location.z(), z), type, metaData);
        if (type != Substance.AIR) {
            int i = WorldUtils.heightIndex(x, z);
            if (heights.get(i) < y) {
                heights.set(i, y);
            }
        }
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
            //noinspection Convert2Lambda Because lamdbda doesn't carry over generic type of method
            return executor.submitTask(new Callable<Block>() {
                @Override
                public Block call() throws Exception{
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

                    TridentBlock block = new TridentBlock(Position.create(world, relX + x() * 16, y, relZ + z() * 16),
                            material, meta);
                    Vector key = new Vector(relX, y, relZ);
                    List<BlockMeta> metas = blockMeta.get(key);
                    if (metas != null) {
                        for (BlockMeta m : metas) {
                            block.applyMeta(m);
                        }
                    }

                    return block;
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            TridentLogger.get().error(e);
            return null;
        }
    }

    @Override
    public ChunkSnapshot snapshot() {
        return new TridentChunkSnapshot(world, this);
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
                        TridentLogger.get().error(e);
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
                        TridentLogger.get().error(e);
                    }
                }

                for (int i = 0; i < 256; i += 1) {
                    data.write(0);
                }

                return new PacketPlayOutChunkData(data.toByteArray(), location, false, (short) bitmask);
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            TridentLogger.get().error(e);
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

        int[] rawHeight = biomes.value();
        for (int i = 0; i < 256; i++) {
            heights.set(i, rawHeight[i]);
        }

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

        int[] rawHeights = new int[256];
        for (int i = 0; i < 256; i++) {
            rawHeights[i] = heights.get(i);
        }

        level.addTag(new IntArrayTag("HeightMap").setValue(rawHeights));

        final ListTag sectionTags = new ListTag("Sections", TagType.COMPOUND);

        ChunkSection[] sectionCopy = new ChunkSection[0];
        try {
            sectionCopy = executor.submitTask(this::mapSections).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        for (ChunkSection section : sectionCopy) {
            section.updateRaw();
            sectionTags.addTag(NBTSerializer.serialize(section));
        }

        level.addTag(sectionTags);

        ListTag tag = new ListTag("Entities", TagType.COMPOUND);
        for (Entity entity : entities()) {
            tag.addTag(((TridentEntity) entity).asNbt());
        }
        level.addTag(tag);

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

    public ArrayList<Entity> getEntities(Entity exclude, BoundingBox boundingBox, Predicate<? super Entity> predicate){
        return new ArrayList<>(this.entities.stream()
                .filter(checking -> checking != exclude && checking.boundingBox().collidesWith(boundingBox))
                .filter(checking -> predicate == null || predicate.test(checking))
                .collect(Collectors.toList()));
    }
}