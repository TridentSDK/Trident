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

import io.netty.util.internal.chmv8.ConcurrentHashMapV8;
import net.tridentsdk.Trident;
import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.meta.nbt.NBTException;
import net.tridentsdk.server.world.gen.DefaultWorldGen;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.world.Chunk;
import net.tridentsdk.world.ChunkLocation;
import net.tridentsdk.world.World;
import net.tridentsdk.world.WorldLoader;
import net.tridentsdk.world.gen.AbstractGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.zip.DataFormatException;

/**
 * The world loading class, creates, saves, handles worlds
 *
 * @author The TridentSDK Team
 */
public class TridentWorldLoader implements WorldLoader {
    private static final Map<String, TridentWorld> worlds = new ConcurrentHashMapV8<>();
    private final AbstractGenerator generator;

    public TridentWorldLoader(AbstractGenerator generator) {
        this.generator = generator;
    }

    public TridentWorldLoader() {
        this(new DefaultWorldGen());
    }

    public Collection<TridentWorld> worlds() {
        return worlds.values();
    }

    // Prevents this reference from escaping during construction
    // besides, user created WorldLoaders should not re-create
    // the world
    @InternalUseOnly
    public void loadAll() {
        for (File file : Trident.fileContainer().toFile().listFiles()) {
            if (!(file.isDirectory()) || file.getName().contains(" "))
                continue;

            boolean isWorld = false;

            for (File f : file.listFiles())
                if (f.getName().equals("level.dat"))
                    isWorld = true;

            if (!(isWorld))
                continue;

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
        TridentWorld w = (TridentWorld) world;
        for (Chunk chunk : w.loadedChunks())
            saveChunk(chunk);
    }

    @Override
    public boolean worldExists(String world) {
        return this.worlds.containsKey(world);
    }

    @Override
    public boolean chunkExists(World world, int x, int z) {
        return new File(world.name() + "/region/", WorldUtils.getRegionFile(x, z)).exists();
    }

    @Override
    public boolean chunkExists(World world, ChunkLocation location) {
        return this.chunkExists(world, location.getX(), location.getZ());
    }

    @Override
    public Chunk loadChunk(World world, int x, int z) {
        return this.loadChunk(world, ChunkLocation.create(x, z));
    }

    @Override
    public TridentChunk loadChunk(World world, ChunkLocation location) {
        try {
            return RegionFile.fromPath(world.name(), location).loadChunkData((TridentWorld) world, location);
        } catch (IOException | DataFormatException | NBTException ex) {
            TridentLogger.error(ex);
        }

        return null;
    }

    @Override
    public void saveChunk(Chunk chunk) {
        // TODO
    }

    @Override
    public AbstractGenerator generator() {
        return generator;
    }
}
