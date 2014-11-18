/*
 *     Trident - A Multithreaded Server Alternative
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.server.world;

import io.netty.util.internal.ConcurrentSet;
import net.tridentsdk.Block;
import net.tridentsdk.Location;
import net.tridentsdk.Material;
import net.tridentsdk.Trident;
import net.tridentsdk.nbt.*;
import net.tridentsdk.reflect.FastClass;
import net.tridentsdk.util.NibbleArray;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.world.Chunk;
import net.tridentsdk.world.ChunkLocation;
import net.tridentsdk.world.Dimension;
import net.tridentsdk.server.entity.TridentEntity;
import net.tridentsdk.server.packets.play.out.PacketPlayOutChunkData;

import java.util.List;
import java.util.Set;

public class TridentChunk implements Chunk {
    private final TridentWorld world;
    private volatile ChunkLocation location;
    private int lastFileAccess;

    private volatile long lastModified;
    private volatile long inhabitedTime;
    private byte lightPopulated;
    private byte terrainPopulated;

    private ChunkSection[] sections;

    private final Set<TridentEntity> entities = new ConcurrentSet<>();

    public TridentChunk(TridentWorld world, int x, int z) {
        this(world, new ChunkLocation(x, z));
    }

    public TridentChunk(TridentWorld world, ChunkLocation coord) {
        this.world = world;
        this.location = coord;
        this.lastFileAccess = 0;
    }

    protected int getLastFileAccess() {
        return this.lastFileAccess;
    }

    protected void setLastFileAccess(int last) {
        this.lastFileAccess = last;
    }

    @Override
    public void generate() {
    }

    @Override
    public int getX() {
        return location.getX();
    }

    @Override
    public int getZ() {
        return location.getZ();
    }

    @Override
    public ChunkLocation getLocation() {
        return this.location;
    }

    @Override
    public TridentWorld getWorld() {
        return this.world;
    }

    @Override
    public Block getBlockAt(int relX, int y, int relZ) {
        int index = WorldUtils.getBlockArrayIndex(relX, y, relZ);

        return new TridentBlock(new Location(this.world, relX + this.getX() * 16, y, relZ + this.getZ() * 16)
                //TODO
                , null, (byte) 0);
    }

    public void write(PacketPlayOutChunkData packet) {
        packet.set("chunkLocation", location);

        int bitmask;
        int count;

        bitmask = (1 << sections.length) - 1;
        count = sections.length;

        int size = 0;
        int sectionSize = ChunkSection.LENGTH * 5 / 2;

        if (world.getDimension() == Dimension.OVERWORLD) {
            sectionSize += ChunkSection.LENGTH / 2;
        }

        size += count * sectionSize + 256;

        byte[] data = new byte[size];
        int pos = 0;

        for (ChunkSection section : sections) {
            for (byte b : section.getTypes()) {
                data[pos++] = (byte) (b & 0xff);
                data[pos++] = (byte) (b >> 8);
            }
        }

        for (ChunkSection section : sections) {
            System.arraycopy(section.blockLight, 0, data, pos, section.blockLight.length);
            pos += section.blockLight.length;
        }

        for (ChunkSection section : sections) {
            System.arraycopy(section.skyLight, 0, data, pos, section.skyLight.length);
            pos += section.skyLight.length;
        }

        for (int i = 0; i < 256; i += 1) {
            data[pos++] = 0;
        }

        if (pos != size) {
            throw new IllegalStateException("Wrote " + pos + " when expected " + size + " bytes");
        }

        packet.set("data", data);
        packet.set("continuous", true);
        packet.set("primaryBitMap", bitmask);
    }

    public void load(CompoundTag tag) {
        TridentLogger logger = Trident.getLogger();

        logger.info("Loading NBT values...");

        IntTag x = tag.getTagAs("xPos");
        IntTag z = tag.getTagAs("zPos");

        LongTag lastModifed = tag.getTagAs("LastModified");
        ByteTag lightPopulated = tag.getTagAs("LightPopulated");
        ByteTag terrainPopulated = tag.getTagAs("TerrainPopulated");

        LongTag inhabitedTime = tag.getTagAs("InhabitedTime");
        ByteArrayTag biomes = tag.getTagAs("HeightMap");

        ListTag sections = tag.getTagAs("Sections");
        ListTag entities = tag.getTagAs("Entities");
        ListTag tileEntities = tag.getTagAs("TileEntities");
        ListTag tileTicks = tag.getTagAs("TileTicks");

        logger.info("Loaded NBT values!");
        logger.info("Deserializing NBT values into Chunk sections...");

        List<NBTTag> sectionsList = sections.listTags();

        this.sections = new ChunkSection[sectionsList.size()];

        /* Load sections */
        for (int i = 0; i <= sectionsList.size(); i += 1) {
            NBTTag t = sections.getTag(i);

            if (t instanceof CompoundTag) {
                CompoundTag ct = (CompoundTag) t;

                this.sections[i] = NBTSerializer.deserialize(ChunkSection.class, ct);
                this.sections[i].loadBlocks();
            }
        }

        logger.info("Deserialized and loaded chunk sections successfully! Loading entities...");

        /* Load Entities */
        FastClass entityClass = FastClass.get(TridentEntity.class);

        for (NBTTag t : entities.listTags()) {
            TridentEntity entity = entityClass.getConstructor().newInstance();

            entity.load((CompoundTag) t);
            this.entities.add(entity);
        }

        logger.info("Deserialized and loaded all entity values successfully! Loading extras...");

        /* Load extras */
        this.lightPopulated = lightPopulated.getValue(); // Unknown use
        this.terrainPopulated = terrainPopulated.getValue(); // if chunk was populated with special things (ores, trees, etc.), if 1 regenerate
        this.lastModified = lastModifed.getValue(); // Tick when the chunk was last saved
        this.inhabitedTime = inhabitedTime.getValue(); // Cumulative number of ticks player have been in the chunk

        logger.info("Successfully loaded extras!");
    }

    public CompoundTag toNbt() {
        return null;
    }

    private final class ChunkSection implements NBTSerializable {

        private static final int LENGTH = 4096; // 16^3 (width * height * depth)

        @NBTField(name = "Blocks", type = TagType.BYTE_ARRAY)
        protected byte[] rawTypes;

        @NBTField(name = "Add", type = TagType.BYTE_ARRAY)
        protected byte[] add;

        @NBTField(name = "Data", type = TagType.BYTE_ARRAY)
        protected byte[] data;

        @NBTField(name = "BlockLight", type = TagType.BYTE_ARRAY)
        protected byte[] blockLight;

        @NBTField(name = "BlockLight", type = TagType.BYTE_ARRAY)
        protected byte[] skyLight;

        private final Block[] blcks = new Block[LENGTH];
        private byte[] types;

        private void loadBlocks() {
            NibbleArray add = new NibbleArray(this.add);
            NibbleArray data = new NibbleArray(this.data);

            types = new byte[rawTypes.length];

            for (int i = 0; i < LENGTH; i += 1) {
                Block block;
                byte b;
                byte bData;
                int bAdd;

                /* Get block data; use extras accordingly */
                b = rawTypes[i];
                bAdd = add.get(i) << 8;
                b += bAdd;
                bData = data.get(i);

                block = new Block(new Location(getWorld(), 0, 0, 0)); // TODO: get none-relative location

                block.setType(Material.fromString(String.valueOf(b)));

                /* TODO get the type and deal with block data accordingly */
                switch (block.getType()) {
                    default:
                        break;
                }

                blcks[i] = block;
                types[i] = (byte) (bAdd | ((b & 0xff) << 4) | bData);
            }
        }

        public byte[] getTypes() {
            return types;
        }

        public Block[] getBlocks() {
            return blcks;
        }
    }
}
