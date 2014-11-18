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
