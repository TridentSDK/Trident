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
import net.tridentsdk.entity.Entity;
import net.tridentsdk.meta.block.BlockMeta;
import net.tridentsdk.meta.block.Tile;
import net.tridentsdk.meta.nbt.*;
import net.tridentsdk.server.chunk.CRefCounter;
import net.tridentsdk.server.chunk.ChunkHandler;
import net.tridentsdk.server.chunk.ConcurrentSectionTable;
import net.tridentsdk.server.entity.TridentEntity;
import net.tridentsdk.server.packets.play.out.PacketPlayOutChunkData;
import net.tridentsdk.util.NibbleArray;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.util.Vector;
import net.tridentsdk.world.Chunk;
import net.tridentsdk.world.ChunkLocation;
import net.tridentsdk.world.ChunkSnapshot;
import net.tridentsdk.world.gen.ChunkGenerator;
import net.tridentsdk.world.gen.FeatureGenerator;
import net.tridentsdk.world.gen.FeatureGenerator.ChunkManipulator;

import javax.annotation.concurrent.GuardedBy;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TridentChunk implements Chunk {
    private final TridentWorld world;
    private final ChunkLocation location;

    @GuardedBy("sections")
    public final ConcurrentSectionTable sections = new ConcurrentSectionTable();
    private final Set<Entity> entities = Sets.newConcurrentHashSet();
    private final Map<Vector, List<BlockMeta>> blockMeta = Maps.newConcurrentMap();
    private final AtomicReferenceArray<Integer> heights = new AtomicReferenceArray<>(256);

    private volatile int lastFileAccess;
    private volatile long lastModified;
    private volatile long inhabitedTime;
    private final AtomicInteger lightPopulated = new AtomicInteger();
    private final AtomicInteger terrainPopulated = new AtomicInteger();

    protected TridentChunk(TridentWorld world, int x, int z) {
        this(world, ChunkLocation.create(x, z));
    }

    protected TridentChunk(TridentWorld world, ChunkLocation coord) {
        this.world = world;
        location = coord;
        lastFileAccess = 0;
        for (int i = 0; i < 256; i++) {
            heights.set(i, 0);
        }
    }

    protected int lastFileAccess() {
        return lastFileAccess;
    }

    protected void setLastFileAccess(int last) {
        lastFileAccess = last;
    }

    @Override
    public boolean isLoaded() {
        return lightPopulated.get() == 0x01 && terrainPopulated.get() == 0x01;
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

    public void gen(boolean withPaint) {
        // Has or is generated already if the state is not 0x00
        if (!lightPopulated.compareAndSet(0x00, 0xFFFFFFFF)) {
            if (withPaint) paint(true);
            return;
        }

        sections.lockFully();
        try {
            ChunkGenerator generator = world.loader().generator();
            char[][] blocks = generator.generateBlocks(location, heights);
            byte[][] data = generator.generateData(location);
            for (int i = 0; i < 16; i++) {
                ChunkSection section = sections.get(i);

                if (blocks != null && blocks.length > 0) {
                    char[] sector = blocks[i];
                    if (sector != null && sector.length > 0) {
                        section.setBlocks(sector);
                    }
                }

                if (data != null && data.length > 0) {
                    byte[] sector = data[i];
                    if (sector != null && sector.length > 0) {
                        section.setData(sector);
                    }
                }

                // DEBUG ===== makes the entire chunk completely lit, not ideal for production
                Arrays.fill(section.skyLight, (byte) 255);
                // =====
                section.updateRaw();
            }

            if (withPaint) {
                paint(false);
            }
        } finally {
            sections.release();
        }

        lightPopulated.set(0x01);
        //TODO lighting
    }

    @Override
    public void generate() {
        gen(true);
    }

    @Override
    public boolean load() {
        if (isLoaded()) {
            return false;
        }

        CompoundTag tag = RegionFile.fromPath(world.name(), location).decode(location);
        if (tag == null) {
            return false;
        }

        load(tag);
        return true;
    }

    public void paint(boolean withLock) {
        // If the state is not 0x00 it is either generating (-1) or has already been
        if (!terrainPopulated.compareAndSet(0x00, 0xFFFFFFFF)) {
            return;
        }

        List<FeatureGenerator> brushes = world.loader().brushes();
        // init chunk event
        ConcurrentHashMap<ChunkLocation, TridentChunk> localCache = new ConcurrentHashMap<>();
        ChunkManipulator manipulator = new ChunkManipulator() {
            @Override
            public void manipulate(int relX, int y, int relZ, Substance substance, byte data) {
                if (relX >= 0 && relX <= 15 && relZ >= 0 && relZ <= 15) {
                    int index = WorldUtils.blockArrayIndex(relX & 15, y & 15, relZ & 15);
                    ChunkSection section = sections.get(WorldUtils.section(y));
                    section.types[index] = (char) (substance.asExtended() & 0xfff0 | data);
                    NibbleArray.set(section.data, index, data);
                    NibbleArray.set(section.skyLight, index, (byte) 255);
                    NibbleArray.set(section.blockLight, index, (byte) 255);
                    return;
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
                TridentChunk chunk = localCache.computeIfAbsent(loc, k -> rawChunk(loc));
                chunk.setAt(newX, y, newZ, substance, data, (byte) 255, (byte) 15);
            }

            @Override
            public Block blockAt(int relX, int y, int relZ) {
                if (relX >= 0 && relX <= 15 && relZ >= 0 && relZ <= 15) {
                    ChunkSection section = sections.get(WorldUtils.section(y));
                    int index = WorldUtils.blockArrayIndex(relX, y & 15, relZ);
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
                TridentChunk chunk = localCache.computeIfAbsent(loc, k -> rawChunk(loc));
                return chunk.blockAt(newX, y, newZ);
            }
        };

        if (withLock) sections.lockFully();
        try {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    for (FeatureGenerator brush : brushes) {
                        brush.generate(location, i, j, world.random(), heights, manipulator);
                    }
                }
            }
        } finally {
            if (withLock) sections.release();
        }

        // Label as populated, so the chunk is not repopulated
        terrainPopulated.set(0x01);
    }

    private TridentChunk rawChunk(ChunkLocation location) {
        return world.chunkAt(location, true);
    }

    private static int up(double d) {
        if (Math.rint(d) != d)
            return (int) d + 1;
        return (int) d;
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
        return location;
    }

    @Override
    public TridentWorld world() {
        return world;
    }

    @Override
    public Block blockAt(int relX, int y, int relZ) {
        int index = WorldUtils.blockArrayIndex(relX, y & 15, relZ);
        int sectionIndex = WorldUtils.section(y);
        return sections.modifyAndReturn(sectionIndex, section -> {
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
        });
    }

    @Override
    public ChunkSnapshot snapshot() {
        return new TridentChunkSnapshot(world, this);
    }

    public PacketPlayOutChunkData asPacket() {
        sections.lockFully();
        try {
            ByteArrayOutputStream data = new ByteArrayOutputStream();

            for (int i = 0; i < 16; i++) {
                ChunkSection section = sections.get(i);
                if (section == null) {
                    continue;
                }

                for (char c : section.types()) {
                    data.write(c & 0xff);
                    data.write(c >> 8);
                }
            }

            for (int i = 0; i < 16; i++) {
                ChunkSection section = sections.get(i);
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

            for (int i = 0; i < 16; i++) {
                ChunkSection section = sections.get(i);
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

            // fixme unused value
            int bitmask = 65535;
            return new PacketPlayOutChunkData(data.toByteArray(), location, false, (short) bitmask);
        } finally {
            sections.release();
        }
    }

    public void load(CompoundTag root) {
        CompoundTag tag = root.getTagAs("Level");
        LongTag lastModifed = tag.getTagAs("LastUpdate");
        ByteTag lightPopulated = tag.containsTag("LightPopulated") ? (ByteTag) tag.getTagAs(
                "LightPopulated") : new ByteTag("LightPopulated").setValue((byte) 0);
        ByteTag terrainPopulated = tag.getTagAs("TerrainPopulated");

        LongTag inhabitedTime = tag.getTagAs("InhabitedTime");
        IntArrayTag biomes = tag.getTagAs("HeightMap");

        int[] rawHeight = biomes.value();
        for (int i = 0; i < 256; i++) {
            heights.set(i, rawHeight[i]);
        }

        ListTag sectionTags = tag.getTagAs("Sections");
        ListTag entities = tag.getTagAs("Entities");
        ListTag tileEntities = tag.containsTag("TileEntities") ? (ListTag) tag.getTag("TileEntities") :
                new ListTag("TileEntities", TagType.COMPOUND);
        ListTag tileTicks = tag.containsTag("TileTicks") ? (ListTag) tag.getTag("TileTicks") : new ListTag(
                "TileTicks", TagType.COMPOUND);
        List<NBTTag> sectionsList = sectionTags.listTags();

        /* Load sections */
        sections.lockFully();
        try {
            for (int i = 0; i < sectionsList.size(); i += 1) {
                NBTTag t = sectionTags.getTag(i);

                if (t instanceof CompoundTag) {
                    CompoundTag ct = (CompoundTag) t;

                    ChunkSection section = NBTSerializer.deserialize(ChunkSection.class, ct);
                    section.loadBlocks();
                    sections.set(section.y(), section);
                }
            }
        } finally {
            sections.release();
        }

        for (NBTTag t : entities.listTags()) {
            //TridentEntity entity = EntityBuilder.create().build(TridentEntity.class);

            //entity.load((CompoundTag) t);
            //world.entities().add(entity);
        }

        /* Load extras */
        this.lightPopulated.set(lightPopulated.value()); // Unknown use
        this.terrainPopulated.set(terrainPopulated.value()); // if chunk was populated with special things (ores,
        // trees, etc.), if 1 regenerate
        lastModified = lastModifed.value(); // Tick when the chunk was last saved
        this.inhabitedTime = inhabitedTime.value(); // Cumulative number of ticks player have been in the chunk
    }

    @Override
    // todo refactor to boolean
    public void unload() {
        sections.lockFully();
        try {
            ChunkHandler chunkHandler = world.chunkHandler();

            // slight inefficacy here - redundant operation when called with
            // ChunkHandler#tryRemove(...)
            CRefCounter refCounter = chunkHandler.get(location);
            if (refCounter != null) {
                if (refCounter.hasStrongRefs()) {
                    return;
                }
            }

            world.loader().saveChunk(this);
            chunkHandler.remove(location);
        } finally {
            sections.release();
        }
    }

    public CompoundTag asNbt() {
        CompoundTag root = new CompoundTag("root");
        CompoundTag level = new CompoundTag("Level");

        level.addTag(new LongTag("LastUpdate").setValue(world.time()));
        level.addTag(new ByteTag("LightPopulated").setValue((byte) lightPopulated.get()));
        level.addTag(new ByteTag("TerrainPopulated").setValue((byte) terrainPopulated.get()));

        level.addTag(new LongTag("InhabitedTime").setValue(inhabitedTime));

        int[] rawHeights = new int[256];
        for (int i = 0; i < 256; i++) {
            rawHeights[i] = heights.get(i);
        }

        level.addTag(new IntArrayTag("HeightMap").setValue(rawHeights));

        ListTag sectionTags = new ListTag("Sections", TagType.COMPOUND);

        for (int i = 0; i < 16; i++) {
            sections.modify(i, section -> {
                section.updateRaw();
                sectionTags.addTag(NBTSerializer.serialize(section));
            });
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

    public void setAt(int x, int y, int z, Substance type, byte metaData, byte skyLight,
                      byte blockLight) {
        int index = WorldUtils.blockArrayIndex(x & 15, y & 15, z & 15);
        sections.modify(WorldUtils.section(y), section -> {
            section.types[index] = (char) (type.asExtended() & 0xfff0 | metaData);
            NibbleArray.set(section.data, index, metaData);
            NibbleArray.set(section.skyLight, index, skyLight);
            NibbleArray.set(section.blockLight, index, blockLight);
        });
    }

    @Override
    public ArrayList<Entity> getEntities(Entity exclude, BoundingBox boundingBox, Predicate<? super Entity> predicate){
        return new ArrayList<>(entities.stream()
                .filter(checking -> !checking.equals(exclude) && checking.boundingBox().collidesWith(boundingBox))
                .filter(checking -> predicate == null || predicate.test(checking))
                .collect(Collectors.toList()));
    }
}