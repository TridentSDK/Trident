/*
 * Copyright (c) 2014, TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of TridentSDK nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.world;

import net.tridentsdk.api.Block;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.nbt.*;
import net.tridentsdk.api.world.Chunk;
import net.tridentsdk.api.world.ChunkLocation;

import java.util.Random;

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
    @Override
    public void generate() {
        // this is just a no, we need chunk generators to be used instead of following on a one generator basis
        int chunkX = this.getX() * 16;
        int chunkZ = this.getZ() * 16;

        Random r = new Random();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                //TODO y

                int y = 0;

                //TODO Place blocks
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

        return new TridentBlock(new Location(world, relX + this.getX()*16, y, relZ + this.getZ()*16)
                //TODO
                ,null,(byte)0);
    }
}
