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
import net.tridentsdk.api.nbt.CompoundTag;
import net.tridentsdk.api.nbt.ListTag;
import net.tridentsdk.api.nbt.NBTField;
import net.tridentsdk.api.nbt.NBTSerializable;
import net.tridentsdk.api.nbt.TagType;
import net.tridentsdk.api.world.Chunk;
import net.tridentsdk.api.world.ChunkLocation;
import net.tridentsdk.api.world.WorldGen;

public class TridentChunk implements Chunk, NBTSerializable {
    private final TridentWorld world;
    private final ChunkLocation location;
    private CompoundTag chunkData;
    private int lastFileAccess;

    @NBTField(name = "xPos", type = TagType.INT)
    protected int x;

    @NBTField(name = "zPos", type = TagType.INT)
    protected int z;

    // the last tick that this chunk was saved on
    @NBTField(name = "LastModified", type = TagType.LONG)
    protected long lastModified;

    @NBTField(name = "LightPopulated", type = TagType.BYTE)
    protected byte lightPopulated;

    @NBTField(name = "TerrainPopulated", type = TagType.BYTE)
    protected byte terrainPopulated;

    @NBTField(name = "InhabitedTime", type = TagType.LONG)
    protected long inhabitedtime;

    @NBTField(name = "Biomes", type = TagType.BYTE_ARRAY)
    protected byte[] biomes;

    @NBTField(name = "HeightMap", type = TagType.INT_ARRAY)
    protected int[] heightMap;

    @NBTField(name = "Sections", type = TagType.LIST)
    protected ListTag sections;

    @NBTField(name = "Entities",type = TagType.LIST)
    protected ListTag entities;

    @NBTField(name = "TileEntities", type = TagType.LIST)
    protected ListTag tileEnts;

    @NBTField(name = "TileTicks", type = TagType.LIST)
    protected ListTag tileTicks;

    public TridentChunk(TridentWorld world, int x, int z) {
        this(world, new ChunkLocation(x, z));
    }

    public TridentChunk(TridentWorld world, ChunkLocation coord) {
        this.world = world;
        this.location = coord;
        this.lastFileAccess = 0;
    }

    protected CompoundTag getData() {
        return this.chunkData;
    }

    protected void setData(CompoundTag tag) {
        if (tag != null) {
            this.chunkData = tag;
        }
    }

    protected int getLastFileAccess() {
        return this.lastFileAccess;
    }

    protected void setLastFileAccess(int last) {
        this.lastFileAccess = last;
    }

    //FIXME: This whole system needs changing
    WorldGen gen = new TempWorldGen();
    @Override
    public void generate() {
        for (int x = this.location.getX(); x <= this.location.getX() >> 4; x++) {
            for (int y = 0; y <= 64 >> 4; y++) {
                for (int z = this.location.getZ(); z <= this.location.getZ() >> 4; z++) {
                    this.getBlockAt(x, y, z).setMaterial(this.gen.gen(x, y, z));
                }
            }
        }
    }

    @Override
    public int getX() {
        return this.location.getX();
    }

    @Override
    public int getZ() {
        return this.location.getX();
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
}
