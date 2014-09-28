/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.world;

import net.tridentsdk.api.nbt.CompoundTag;
import net.tridentsdk.api.world.Chunk;
import net.tridentsdk.api.world.ChunkLocation;

import java.io.Serializable;
import java.util.Random;

public class TridentChunk implements Serializable, Chunk {
    private static final long serialVersionUID = 3323137810332318805L;
    private final TridentWorld world;
    private final ChunkLocation location;
    private CompoundTag chunkData;
    private int lastFileAccess;

    public TridentChunk(TridentWorld world, int x, int z) {
        this(world, new ChunkLocation(x, z));
    }

    public TridentChunk(TridentWorld world, ChunkLocation coord) {
        this.world = world;
        this.location = coord;
        this.lastFileAccess = 0;
    }

    protected CompoundTag getData() {
        return chunkData;
    }

    protected void setData(CompoundTag tag) {
        if (tag != null) {
            this.chunkData = tag;
        }
    }

    protected int getLastFileAccess() {
        return lastFileAccess;
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
}
