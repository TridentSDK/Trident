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

import com.google.common.base.Preconditions;
import net.tridentsdk.Trident;
import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.server.world.gen.DefaultWorldGen;
import net.tridentsdk.server.world.gen.brush.OakTreeBrush;
import net.tridentsdk.server.world.gen.brush.TallGrassBrush;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.world.Chunk;
import net.tridentsdk.world.ChunkLocation;
import net.tridentsdk.world.World;
import net.tridentsdk.world.WorldLoader;
import net.tridentsdk.world.gen.ChunkGenerator;
import net.tridentsdk.world.gen.FeatureGenerator;
import net.tridentsdk.world.settings.WorldCreateOptions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The world loading class, creates, saves, handles worlds
 *
 * @author The TridentSDK Team
 */
public class TridentWorldLoader implements WorldLoader {
    private static final ChunkGenerator DEFAULT_GEN = new DefaultWorldGen(ThreadLocalRandom.current().nextLong());
    public static final Map<String, TridentWorld> WORLDS = new ConcurrentHashMap<>();

    private final WorldCreateOptions opt;
    private final List<FeatureGenerator> brushes = new CopyOnWriteArrayList<>();
    private volatile ChunkGenerator generator;
    volatile TridentWorld world;

    public TridentWorldLoader(WorldCreateOptions opt) {
        this.opt = opt;
    }

    public TridentWorldLoader() {
        this.opt = new WorldCreateOptions();
        this.opt.generator(DefaultWorldGen.class);
    }

    public Collection<TridentWorld> worlds() {
        return WORLDS.values();
    }

    // Prevents this reference from escaping during construction
    // besides, user created WorldLoaders should not re-create
    // the world
    @InternalUseOnly
    public static void loadAll() {
        TridentLogger.get().log("Loading worlds...");
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
                        if (!className.equals(DEFAULT_GEN.getClass().getName())) {
                            // Create a new loader with that class, don't load it with this one
                            new TridentWorldLoader(Class.forName(className).asSubclass(WorldCreateOptions.class).newInstance())
                                    .load(file.getName());
                            isWorld = false;
                        }
                    } catch (IOException e) {
                        TridentLogger.get().error(e);
                        isWorld = true;
                    } catch (ClassNotFoundException e) {
                        TridentLogger.get().error("Could not find loader " + className + ", resorting to default");
                        TridentLogger.get().error(e);

                        // Nevermind, load with this one anyways
                        isWorld = true;
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (!(isWorld))
                continue;

            Path gensig = Trident.fileContainer().resolve(file.getName()).resolve("gensig");
            if (!Files.exists(gensig)) {
                try {
                    Files.createFile(gensig);
                    Files.write(gensig, DEFAULT_GEN.getClass().getName().getBytes(Charset.defaultCharset()));
                } catch (IOException e) {
                    TridentLogger.get().error("Could not write gensig file");
                    TridentLogger.get().error(e);
                }
            }
            new TridentWorldLoader().load(file.getName());
        }
        if (WORLDS.size() == 0) {
            TridentLogger.get().error("No worlds found, there is no world loaded!");
        }
        TridentLogger.get().log("Finished loading worlds!");
    }

    @Override
    public World load(String world) {
        Preconditions.checkArgument(this.world == null, "This WorldLoader has already loadd a world");
        TridentWorld w = new TridentWorld(world, this);
        WORLDS.put(world, w);

        return w;
    }

    @Override
    public void save() {
        checkNotNull();
        world.save();
        // TODO save player and entity data
        // consider saving the STATE instead
    }

    @Override
    public World createWorld(String name) {
        Preconditions.checkArgument(world == null, "This WorldLoader has already loaded a world");

        if (WorldLoader.worldExists(name)) {
            TridentLogger.get().error(new IllegalArgumentException("Cannot create a duplicate world name"));
            return null;
        }

        // TODO load world settings
        TridentWorld world = TridentWorld.createWorld(name, this);
        WORLDS.put(name, world);

        return world;
    }

    @Override
    public boolean chunkExists(int x, int z) {
        checkNotNull();
        return new File(world.name() + "/region/", WorldUtils.regionFile(x, z)).exists();
    }

    @Override
    public boolean chunkExists(ChunkLocation location) {
        return this.chunkExists(location.x(), location.z());
    }

    @Override
    public Chunk loadChunk(int x, int z) {
        return this.loadChunk(ChunkLocation.create(x, z));
    }

    @Override
    public TridentChunk loadChunk(ChunkLocation location) {
        checkNotNull();
        return RegionFile.fromPath(world.name(), location).loadChunkData(world, location);
    }

    @Override
    public void saveChunk(Chunk chunk) {
        RegionFile.fromPath(chunk.world().name(), chunk.location())
                .saveChunkData((TridentChunk) chunk);
    }

    @Override
    public WorldCreateOptions options() {
        return opt;
    }

    public void setGenerator(long seed) {
        ChunkGenerator gen;
        Class<? extends ChunkGenerator> generatorClass = opt.generator();
        try {
            Constructor<? extends ChunkGenerator> g = generatorClass.getDeclaredConstructor(long.class);
            gen = g.newInstance(seed);
            Collections.addAll(brushes, new TallGrassBrush(seed), new OakTreeBrush(seed));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            TridentLogger.get().error("Error occurred while instantiating generator " + generatorClass.getName());
            TridentLogger.get().error("Switching to the default");
            TridentLogger.get().error(e);
            gen = DEFAULT_GEN;
        } catch (NoSuchMethodException e) {
            TridentLogger.get().error("Provided generator does not have a default constructor");
            TridentLogger.get().error("Switching to the default");
            TridentLogger.get().error(e);
            gen = DEFAULT_GEN;
        }

        this.generator = gen;
    }

    @Override
    public ChunkGenerator generator() {
        return generator;
    }

    @Override
    public List<FeatureGenerator> brushes() {
        return brushes;
    }

    private void checkNotNull() {
        Preconditions.checkArgument(world != null, "The current world must not be null");
    }
}