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
package net.tridentsdk.world;

import net.tridentsdk.api.nbt.NBTException;
import net.tridentsdk.api.world.Chunk;
import net.tridentsdk.api.world.ChunkLocation;
import net.tridentsdk.api.world.World;
import net.tridentsdk.api.world.WorldLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.DataFormatException;

public class TridentWorldLoader implements WorldLoader {
    private final Map<String, TridentWorld> worlds = new ConcurrentHashMap<>();

    public TridentWorldLoader() {
        for (File file : getWorldContainer().listFiles()) {
            if (!(file.isDirectory()) || file.getName().contains(" ")) {
                continue;
            }

            boolean isWorld = false;

            for (File f : file.listFiles()) {
                if (f.getName().equals("level.dat")) {
                    isWorld = true;
                }
            }

            if (!(isWorld)) {
                continue;
            }

            load(file.getName());
        }
    }

    @Override
    public World load(String world) {
        TridentWorld w = new TridentWorld(world, this);

        worlds.put(world, w);
        return w;
    }

    @Override
    public void save(World world) {
        // TODO
    }

    public Collection<TridentWorld> getWorlds() {
        return worlds.values();
    }

    @Override
    public boolean worldExists(String world) {
        return this.worlds.containsKey(world);
    }

    @Override
    public boolean chunkExists(World world, int x, int z) {
        return new File(world.getName() + "/region/", WorldUtils.getRegionFile(x, z)).exists();
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
    public TridentChunk loadChunk(World world, ChunkLocation location) {
        try {
            RegionFile file =
                    new RegionFile(FileSystems.getDefault().getPath(
                            world.getName() + "/region/", WorldUtils.getRegionFile(location)));

            return file.loadChunkData((TridentWorld) world, location);
        } catch (IOException | DataFormatException | NBTException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public void saveChunk(Chunk chunk) {
        // TODO
    }

    public File getWorldContainer() {
        return new File(System.getProperty("user.dir"));
    }
}
