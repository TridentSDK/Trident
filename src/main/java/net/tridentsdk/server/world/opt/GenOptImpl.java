/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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
package net.tridentsdk.server.world.opt;

import net.tridentsdk.server.world.gen.FlatGeneratorProvider;
import net.tridentsdk.world.gen.GeneratorProvider;
import net.tridentsdk.world.opt.GenOpts;
import net.tridentsdk.world.opt.LevelType;
import net.tridentsdk.world.opt.WorldCreateSpec;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Implementation of the generator options interface.
 */
public class GenOptImpl implements GenOpts {
    /**
     * The seed source for the current instance of the
     * server
     */
    private static final Random SEED_SRC = new Random();
    private final GeneratorProvider provider;
    private final long seed;
    private final String seedInput;
    private final LevelType type;
    private final boolean allowFeatures;

    /**
     * Default constructor for generating a world with
     * the normal options.
     */
    public GenOptImpl() {
        this(FlatGeneratorProvider.INSTANCE, 0, "", LevelType.FLAT, true);
    }

    /**
     * Constructor for generating a world custom to the
     * given specifications.
     */
    public GenOptImpl(WorldCreateSpec spec) {
        this();
    }

    /**
     * Constructor for loading the given NBT options into
     * this set of options.
     */
    public GenOptImpl(Object o) {
        this();
    }

    /**
     * Custom constructor for customizing the generator
     * options of the world to be generated.
     *
     * @param provider the generator
     * @param seed the seed
     * @param seedInput the input that created the seed
     * @param level the level type
     * @param allowFeatures whether or not features are
     * done
     */
    public GenOptImpl(GeneratorProvider provider, int seed, String seedInput, LevelType level, boolean allowFeatures) {
        this.provider = provider;
        this.type = level;
        this.allowFeatures = allowFeatures;

        if (seed == 0) {
            long potentialSeed;
            while ((potentialSeed = SEED_SRC.nextLong()) == 0)
                ;
            this.seed = potentialSeed;
            this.seedInput = String.valueOf(potentialSeed);
        } else {
            this.seed = seed;
            this.seedInput = seedInput;
        }
    }

    @Override
    public GeneratorProvider provider() {
        return this.provider;
    }

    @Override
    public long seed() {
        return this.seed;
    }

    @Nonnull
    @Override
    public String seedInput() {
        return this.seedInput;
    }

    @Override
    public LevelType levelType() {
        return this.type;
    }

    @Override
    public boolean allowFeatures() {
        return this.allowFeatures;
    }

    /**
     * Saves the world options as NBT data.
     */
    public void save() {
    }
}