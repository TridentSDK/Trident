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
package net.tridentsdk.world;

import net.tridentsdk.api.Block;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.Material;
import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.nbt.*;
import net.tridentsdk.api.util.NibbleArray;
import net.tridentsdk.api.world.Chunk;
import net.tridentsdk.api.world.ChunkLocation;
import net.tridentsdk.packets.play.out.PacketPlayOutChunkData;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class TridentChunk implements Chunk {
    private final TridentWorld world;
    private volatile ChunkLocation location;
    private int lastFileAccess;

    private volatile int lastModified;
    private byte lightPopulated;
    private byte terrainPopulated;

    private ChunkSection[] sections;

    private final Set<Entity> entities = new ConcurrentSkipListSet<>(); // TODO: confirm if correct set implementation

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
                , null, (byte)0);
    }

    public void write(PacketPlayOutChunkData packet) {

    }

    public void load(CompoundTag tag) {
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

        List<NBTTag> sectionsList = sections.listTags();

        this.sections = new ChunkSection[sectionsList.size()];

        /* Load sections */
        for(int i = 0; i <= sectionsList.size(); i += 1) {
            NBTTag t = sections.getTag(i);

            if(t instanceof CompoundTag) {
                CompoundTag ct = (CompoundTag) t;

                this.sections[i] = NBTSerializer.deserialize(ChunkSection.class, ct);
            }
        }
    }

    public CompoundTag toNbt() {
        return null;
    }

    private final class ChunkSection implements NBTSerializable {

        private static final int LENGTH = 4096; // 16^3 (width * height * depth)

        @NBTField(name = "Blocks", type = TagType.BYTE_ARRAY)
        protected byte[] blocks;

        @NBTField(name = "Add", type = TagType.BYTE_ARRAY)
        protected byte[] add;

        @NBTField(name = "Data", type = TagType.BYTE_ARRAY)
        protected byte[] data;

        @NBTField(name = "BlockLight", type = TagType.BYTE_ARRAY)
        protected byte[] blockLight;

        @NBTField(name = "BlockLight", type = TagType.BYTE_ARRAY)
        protected byte[] skyLight;

        private final Block[] blcks = new Block[LENGTH];

        private void loadBlocks() {
            NibbleArray add = new NibbleArray(this.add);
            NibbleArray data = new NibbleArray(this.data);

            for(int i = 0; i < LENGTH; i += 1) {
                Block block;
                byte b;
                byte bData;

                /* Get block data; use extras accordingly */
                b = blocks[i];
                b += add.get(i) << 8;
                bData = data.get(i);

                block = new Block(new Location(getWorld(), 0, 0, 0)); // TODO: get none-relative location

                block.setType(Material.fromString(String.valueOf(b)));

                /* TODO get the type and deal with block data accordingly */
                switch(block.getType()) {
                    default:
                        break;
                }

                blcks[i] = block;
            }
        }
    }
}
