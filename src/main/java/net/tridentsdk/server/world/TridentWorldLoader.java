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
import com.google.common.collect.Sets;
import net.tridentsdk.Trident;
import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.server.world.gen.DefaultWorldGen;
import net.tridentsdk.server.world.gen.brush.OakTreeBrush;
import net.tridentsdk.server.world.gen.brush.TallGrassBrush;
import net.tridentsdk.util.FastRandom;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.world.Chunk;
import net.tridentsdk.world.ChunkLocation;
import net.tridentsdk.world.World;
import net.tridentsdk.world.WorldLoader;
import net.tridentsdk.world.gen.AbstractGenerator;
import net.tridentsdk.world.gen.AbstractOverlayBrush;
import net.tridentsdk.world.settings.Difficulty;
import net.tridentsdk.world.settings.Dimension;
import net.tridentsdk.world.settings.GameMode;
import net.tridentsdk.world.settings.LevelType;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The world loading class, creates, saves, handles worlds
 *
 * @author The TridentSDK Team
 */
public class TridentWorldLoader implements WorldLoader {
    private static final AbstractGenerator DEFAULT_GEN = new DefaultWorldGen(ThreadLocalRandom.current().nextLong());
    public static final Map<String, TridentWorld> WORLDS = new ConcurrentHashMap<>();

    private final Class<? extends AbstractGenerator> generatorClass;
    private final List<AbstractOverlayBrush> brushes = new CopyOnWriteArrayList<>();
    private volatile AbstractGenerator generator;
    volatile TridentWorld world;

    public TridentWorldLoader(Class<? extends AbstractGenerator> generator) {
        this.generatorClass = generator;
    }

    public TridentWorldLoader() {
        this(DEFAULT_GEN.getClass());
    }

    public Collection<TridentWorld> worlds() {
        return WORLDS.values();
    }

    // Prevents this reference from escaping during construction
    // besides, user created WorldLoaders should not re-create
    // the world
    @InternalUseOnly
    public static void loadAll() {
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
                        if (!className.equals(DEFAULT_GEN.getClass().getName())) {
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
                    Files.write(gensig, DEFAULT_GEN.getClass().getName().getBytes(Charset.defaultCharset()));
                } catch (IOException e) {
                    TridentLogger.error("Could not write gensig file");
                    TridentLogger.error(e);
                }
            }
            new TridentWorldLoader().load(file.getName());
        }
        if (WORLDS.size() == 0) {
            TridentLogger.error("No worlds found, there is no world loaded!");
        }
        TridentLogger.log("Finished loading worlds!");
    }

    @Override
    public World load(String world) {
        checkNull();
        TridentWorld w = new TridentWorld(world, this);
        WORLDS.put(world, w);

        return w;
    }

    @Override
    public void save() {
        checkNotNull();
        world.loadedChunks().forEach(this::saveChunk);
        // TODO save player and entity data
        // consider saving the STATE instead
    }

    @Override
    public World createWorld(String name) {
        if (WorldLoader.worldExists(name)) {
            TridentLogger.error(new IllegalArgumentException("Cannot create a duplicate world name"));
            return null;
        }

        checkNull();

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

    public void setGenerator(long seed) {
        AbstractGenerator gen;
        try {
            Constructor<? extends AbstractGenerator> g = generatorClass.getDeclaredConstructor(long.class);
            gen = g.newInstance(seed);
            Collections.addAll(brushes, new TallGrassBrush(seed), new OakTreeBrush(seed));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            TridentLogger.error("Error occurred while instantiating generator " + generatorClass.getName());
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

    @Override
    public AbstractGenerator generator() {
        return generator;
    }

    @Override
    public List<AbstractOverlayBrush> brushes() {
        return brushes;
    }

    Dimension dimension = Dimension.OVERWORLD;
    Difficulty difficulty = Difficulty.PEACEFUL;
    GameMode gameMode = GameMode.SURVIVAL;
    LevelType levelType = LevelType.DEFAULT;
    Set<String> rules = Sets.newHashSet();
    boolean structures = true;
    long seed = FastRandom.random();

    @Override
    public WorldLoader seed(long seed) {
        checkNull();
        this.seed = seed;
        return this;
    }

    @Override
    public WorldLoader dimension(Dimension dimension) {
        checkNull();
        this.dimension = dimension;
        return this;
    }

    @Override
    public WorldLoader difficulty(Difficulty difficulty) {
        checkNull();
        this.difficulty = difficulty;
        return this;
    }

    @Override
    public WorldLoader gameMode(GameMode gameMode) {
        checkNull();
        this.gameMode = gameMode;
        return this;
    }

    @Override
    public WorldLoader level(LevelType levelType) {
        checkNull();
        this.levelType = levelType;
        return this;
    }

    @Override
    public WorldLoader rule(String... rules) {
        checkNull();
        for (String s : rules) this.rules.add(s);
        return this;
    }

    @Override
    public WorldLoader structures(boolean gen) {
        checkNull();
        this.structures = gen;
        return this;
    }

    //////////////////////////////// DELEGATES

    @Override
    public long seed() {
        checkNotNull();
        return seed;
    }

    @Override
    public Dimension dimension() {
        checkNotNull();
        return dimension;
    }

    @Override
    public Difficulty difficulty() {
        checkNotNull();
        return difficulty;
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
        checkNotNull();
        world.setDifficulty(difficulty);
    }

    @Override
    public GameMode defaultGameMode() {
        checkNotNull();
        return gameMode;
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        checkNotNull();
        world.setGameMode(gameMode);
    }

    @Override
    public LevelType levelType() {
        checkNotNull();
        return levelType;
    }

    @Override
    public boolean isRule(String rule) {
        checkNotNull();
        return rules.contains(rule);
    }

    @Override
    public Set<String> gameRules() {
        checkNotNull();
        return rules;
    }

    @Override
    public boolean generateStructures() {
        checkNotNull();
        return structures;
    }

    private void checkNotNull() {
        Preconditions.checkNotNull(world, "This world loader does not have a loaded world");
    }

    private void checkNull() {
        Preconditions.checkArgument(world == null, "The current world must be null");
    }
}
