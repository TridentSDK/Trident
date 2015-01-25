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

import com.google.common.collect.Lists;
import net.tridentsdk.Coordinates;
import net.tridentsdk.base.Block;
import net.tridentsdk.base.Substance;
import net.tridentsdk.meta.nbt.*;
import net.tridentsdk.server.packets.play.out.PacketPlayOutChunkData;
import net.tridentsdk.util.NibbleArray;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.world.Chunk;
import net.tridentsdk.world.ChunkLocation;
import net.tridentsdk.world.ChunkSnapshot;
import net.tridentsdk.world.Dimension;
import net.tridentsdk.world.gen.WorldGenHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class TridentChunk implements Chunk {
    private final TridentWorld world;
    private final ChunkLocation location;
    private final ByteArrayOutputStream data = new ByteArrayOutputStream();
    private volatile int lastFileAccess;
    private volatile long lastModified;
    private volatile long inhabitedTime;
    private volatile byte lightPopulated;
    private volatile byte terrainPopulated;
    private volatile ChunkSection[] sections;

    public TridentChunk(TridentWorld world, int x, int z) {
        this(world, ChunkLocation.create(x, z));
    }

    public TridentChunk(TridentWorld world, ChunkLocation coord) {
        this.world = world;
        this.location = coord;
        this.lastFileAccess = 0;
    }

    protected int lastFileAccess() {
        return this.lastFileAccess;
    }

    protected void setLastFileAccess(int last) {
        this.lastFileAccess = last;
    }

    @Override
    public void generate() {
        WorldGenHandler handler = WorldGenHandler.create(world.loader().generator());
        handler.apply(world, location, location);
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
    public Block tileAt(int relX, int y, int relZ) {
        int index = WorldUtils.blockArrayIndex(relX, y % 16, relZ);
        ChunkSection section = sections[WorldUtils.section(y)];
        NibbleArray add = new NibbleArray(section.add);
        NibbleArray data = new NibbleArray(section.data);

        /* Get block data; use extras accordingly */
        byte b = section.rawTypes[index];
        int bAdd = add.get(index) << 8;
        byte meta = data.get(index);
        b += bAdd;

        Substance material = Substance.fromId(b);

        if (material == null) {
            material = Substance.AIR; // check if valid
        }

        return new TridentBlock(Coordinates.create(this.world, relX + this.x() * 16, y, relZ + this.z() * 16),
                material, meta);
    }

    @Override
    public ChunkSnapshot snapshot() {
        List<CompoundTag> sections = Lists.newArrayList();

        for (ChunkSection section : this.sections) {
            sections.add(NBTSerializer.serialize(section));
        }

        return new TridentChunkSnapshot(world, location, sections, lastFileAccess, lastModified, inhabitedTime,
                lightPopulated, terrainPopulated);
    }

    public PacketPlayOutChunkData asPacket() {
        PacketPlayOutChunkData packet = new PacketPlayOutChunkData();

        if(sections == null) {
            try {
                RegionFile.fromPath(world.name(), location).loadChunkData(this);
            } catch (Exception e) {
                TridentLogger.error(e);
            }
        }

        int bitmask = (1 << sections.length) - 1;
        int count = sections.length;
        int size = 0;
        int sectionSize = ChunkSection.LENGTH * 5 / 2;

        if (world.dimension() == Dimension.OVERWORLD)
            sectionSize += ChunkSection.LENGTH / 2;

        size += count * sectionSize + 256;

        //byte[] data = new byte[size];
        //int pos = 0;

        for (ChunkSection section : sections) {
            if (section == null)
                continue;

            for (byte b : section.types()) {
                data.write(b & 0xff);
                data.write(b >> 8);
            }
        }

        for (ChunkSection section : sections) {
            try {
                data.write(section.blockLight);
            } catch (IOException e) {
                TridentLogger.error(e);
            }
        }

        for (ChunkSection section : sections) {
            try {
                data.write(section.skyLight);
            } catch (IOException e) {
                TridentLogger.error(e);
            }
        }

        for (int i = 0; i < 256; i += 1) {
            data.write(0);
        }

        /*if (pos != size) {
            TridentLogger.error(new IllegalArgumentException("Pos: " + pos + " does not equal size: " + size));
            return null;
        } */

        packet.set("chunkLocation", location);
        packet.set("bitmask", (short) bitmask);
        packet.set("data", data.toByteArray());

        data.reset();

        return packet;
    }

    public void load(CompoundTag root) {
        CompoundTag tag = root.getTagAs("Level");
        LongTag lastModifed = tag.getTagAs("LastUpdate");
        ByteTag lightPopulated = (tag.containsTag("LightPopulated")) ? (ByteTag) tag.getTagAs(
                "LightPopulated") : new ByteTag("LightPopulated").setValue((byte) 0);
        ByteTag terrainPopulated = tag.getTagAs("TerrainPopulated");

        LongTag inhabitedTime = tag.getTagAs("InhabitedTime");
        IntArrayTag biomes = tag.getTagAs("HeightMap");

        ListTag sections = tag.getTagAs("Sections");
        ListTag entities = tag.getTagAs("Entities");
        ListTag tileEntities = tag.getTagAs("TileEntities");
        ListTag tileTicks = (tag.containsTag("TileTicks")) ? (ListTag) tag.getTag("TileTicks") : new ListTag(
                "TileTicks", TagType.COMPOUND);
        List<NBTTag> sectionsList = sections.listTags();

        this.sections = new ChunkSection[sectionsList.size()];

        /* Load sections */
        for (int i = 0; i < sectionsList.size(); i += 1) {
            NBTTag t = sections.getTag(i);

            if (t instanceof CompoundTag) {
                CompoundTag ct = (CompoundTag) t;

                ChunkSection section = NBTSerializer.deserialize(ChunkSection.class, ct);

                section.loadBlocks(world());
                this.sections[section.y()] = section;
            }
        }

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

    public CompoundTag asNbt() {
        CompoundTag root = new CompoundTag("root");
        CompoundTag level = new CompoundTag("level");

        level.addTag(new LongTag("LastUpdate").setValue(world.time()));
        level.addTag(new ByteTag("LightPopulated").setValue(lightPopulated));
        level.addTag(new ByteTag("TerrainPopulated").setValue(terrainPopulated));

        level.addTag(new LongTag("InhabitedTime").setValue(inhabitedTime));
        level.addTag(new IntArrayTag("HeightMap").setValue(new int[1024])); // placeholder TODO

        ListTag sections = new ListTag("Sections", TagType.COMPOUND);

        for (ChunkSection section : this.sections) {
            sections.addTag(NBTSerializer.serialize(section));
        }

        level.addTag(sections);
        level.addTag(new ListTag("Entities", TagType.COMPOUND)); // another placeholder TODO

        root.addTag(level);

        return root;
    }
}
