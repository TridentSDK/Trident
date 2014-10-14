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

import net.tridentsdk.api.nbt.NBTException;
import net.tridentsdk.api.world.Chunk;
import net.tridentsdk.api.world.ChunkLocation;
import net.tridentsdk.api.world.World;
import net.tridentsdk.api.world.WorldLoader;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.DataFormatException;

public class TridentWorldLoader implements WorldLoader {
    private final Map<String, World> worlds = new ConcurrentHashMap<>();

    @Override
    public World load(String world) {
        return new TridentWorld(world, this);
    }

    @Override
    public void save(World world) {
        // TODO
    }

    @Override
    public boolean worldExists(String world) {
        return this.worlds.containsKey(world);
    }

    //TODO: I don't believe this is checking the right thing... This should be checking of it
    //exist in the save file, not in memory
    @Override
    public boolean chunkExists(World world, int x, int z) {
        return world.getChunkAt(x, z, false) != null;
    }

    @Override
    public boolean chunkExists(World world, ChunkLocation location) {
        return this.chunkExists(world, location.getX(), location.getZ());
    }

    @Override
    public Chunk loadChunk(World world, int x, int z) {
        return this.loadChunk(world, new ChunkLocation(x, z));
    }

    @Override
    public Chunk loadChunk(World world, ChunkLocation location) {
        try {
            RegionFile file =
                    new RegionFile(FileSystems.getDefault().getPath(
                            world.getName() + "/region/", WorldUtils.getRegionFile(location)));
            TridentChunk chunk = new TridentChunk((TridentWorld) world, location);

            file.loadChunkData(chunk);
            return chunk;
        } catch (IOException | DataFormatException | NBTException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveChunk(Chunk chunk) {
        // TODO
    }
}
