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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.DataFormatException;

/**
 * The world loading class, creates, saves, handles worlds
 *
 * @author The TridentSDK Team
 */
public class TridentWorldLoader implements WorldLoader {
    private static final AbstractGenerator DEFAULT_GEN = new DefaultWorldGen();
    private static final Map<String, TridentWorld> worlds = new ConcurrentHashMap<>();
    private final AbstractGenerator generator;

    public TridentWorldLoader(Class<? extends AbstractGenerator> generator) {
        AbstractGenerator gen;
        try {
            Constructor<? extends AbstractGenerator> g = generator.getDeclaredConstructor();
            gen = g.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            TridentLogger.error("Error occurred while instantiating generator " + generator.getName());
            TridentLogger.error("Switching to the default");
            TridentLogger.error(e);
            gen = DEFAULT_GEN;
        } catch (NoSuchMethodException e) {
            TridentLogger.error("Provided generator does not have a default constructor");
            TridentLogger.error("Switching to the default");
            TridentLogger.error(e);
            gen = DEFAULT_GEN;
        }

        this.generator = gen;
    }

    public TridentWorldLoader() {
        this(DEFAULT_GEN.getClass());
    }

    public Collection<TridentWorld> worlds() {
        return worlds.values();
    }

    // Prevents this reference from escaping during construction
    // besides, user created WorldLoaders should not re-create
    // the world
    @InternalUseOnly
    public void loadAll() {
        TridentLogger.log("Loading worlds...");
        for (File file : Trident.fileContainer().toFile().listFiles()) {
            if (!(file.isDirectory()) || file.getName().contains(" "))
                continue;

            boolean isWorld = false;

            for (File f : file.listFiles()) {
                if (f.getName().equals("level.dat")) {
                    isWorld = true;
                    continue;
                }

                if (f.getName().equals("gensig")) {
                    String className = null;

                    try {
                        byte[] sig = Files.readAllBytes(
                                Trident.fileContainer().resolve(file.getName()).resolve("gensig"));
                        className = new String(sig);
                        if (!className.equals(this.getClass().getName())) {
                            // Create a new loader with that class, don't load it with this one
                            new TridentWorldLoader(Class.forName(className).asSubclass(AbstractGenerator.class))
                                    .load(file.getName());
                            isWorld = false;
                        }
                    } catch (IOException e) {
                        TridentLogger.error(e);
                        isWorld = true;
                    } catch (ClassNotFoundException e) {
                        TridentLogger.error("Could not find loader " + className + ", resorting to default");
                        TridentLogger.error(e);

                        // Nevermind, load with this one anyways
                        isWorld = true;
                    }
                }
            }

            if (!(isWorld))
                continue;

            Path gensig = Trident.fileContainer().resolve(file.getName()).resolve("gensig");
            if (!Files.exists(gensig)) {
                try {
                    Files.createFile(gensig);
                    Files.write(gensig, generator().getClass().getName().getBytes(Charset.defaultCharset()));
                } catch (IOException e) {
                    TridentLogger.error("Could not write gensig file");
                    TridentLogger.error(e);
                }
            }
            load(file.getName());
        }
        if (worlds.size() == 0) {
            TridentLogger.error("No worlds found, there is no world loaded!");
        }
        TridentLogger.log("Finished loading worlds!");
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

        w.loadedChunks().forEach(this::saveChunk);
    }

    @Override
    public World createWorld(String name) {
        if (worldExists(name)) {
            TridentLogger.error(new IllegalArgumentException("Cannot create a duplicate world name"));
            return null;
        }

        TridentWorld world = TridentWorld.createWorld(name, this);
        worlds.put(name, world);

        return world;
    }

    @Override
    public boolean worldExists(String world) {
        return worlds.containsKey(world);
    }

    @Override
    public boolean chunkExists(World world, int x, int z) {
        return new File(world.name() + "/region/", WorldUtils.regionFile(x, z)).exists();
    }

    @Override
    public boolean chunkExists(World world, ChunkLocation location) {
        return this.chunkExists(world, location.x(), location.z());
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
        try {
            RegionFile.fromPath(chunk.world().name(), chunk.location())
                    .saveChunkData((TridentChunk) chunk);
        } catch (IOException | NBTException ex) {
            TridentLogger.error(ex);
        }
    }

    @Override
    public AbstractGenerator generator() {
        return generator;
    }
}
