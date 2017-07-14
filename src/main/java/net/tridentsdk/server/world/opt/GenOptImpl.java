/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
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

import lombok.Getter;
import net.tridentsdk.meta.nbt.Tag;
import net.tridentsdk.server.world.gen.FlatGeneratorProvider;
import net.tridentsdk.world.gen.GeneratorProvider;
import net.tridentsdk.world.opt.GenOpts;
import net.tridentsdk.world.opt.LevelType;
import net.tridentsdk.world.opt.WorldCreateSpec;

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

    @Getter
    private final GeneratorProvider provider;
    @Getter
    private final long seed;
    @Getter
    private final String optionString;
    @Getter
    private final LevelType levelType;
    @Getter
    private final boolean allowFeatures;

    /**
     * Constructor for generating a world custom to the
     * given specifications.
     */
    public GenOptImpl(WorldCreateSpec spec) {
        if (spec.isDefault()) {
            this.provider = FlatGeneratorProvider.INSTANCE;
            this.levelType = LevelType.FLAT;
            this.optionString = "";
            this.allowFeatures = true;
            this.seed = verifySeed(0);
        } else {
            this.provider = spec.getProvider() == null ? FlatGeneratorProvider.INSTANCE : spec.getProvider();
            this.levelType = spec.getLevelType();
            this.optionString = spec.getOptionString();
            this.allowFeatures = spec.isAllowFeatures();
            this.seed = verifySeed(spec.getSeed());
        }
    }

    /**
     * Constructor for loading the given NBT options into
     * this set of options.
     */
    public GenOptImpl(Tag.Compound compound) {
        String providerClass = compound.get("TridentProvider");
        if (providerClass != null) {
            try {
                this.provider = (GeneratorProvider) Class.forName(providerClass).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
               throw new RuntimeException(e);
            }
        } else {
            this.provider = FlatGeneratorProvider.INSTANCE;
        }

        this.seed = compound.getLong("RandomSeed");
        this.levelType = LevelType.from(compound.getString("generatorName"));
        this.optionString = compound.getString("generatorOptions");
        this.allowFeatures = compound.getByte("MapFeatures") == 1;
    }

    /**
     * Verifies the seed, ensuring that it is not 0 as that
     * will fuck with the RNG functions. If it is 0, tries
     * to calculate a new one.
     *
     * @param seed a possibly non-zero seed
     * @return a non-zero seed
     */
    private static long verifySeed(long seed) {
        if (seed == 0) {
            long potentialSeed;
            while ((potentialSeed = SEED_SRC.nextLong()) == 0);
            return potentialSeed;
        } else {
            return seed;
        }
    }

    /**
     * Saves the world options as NBT data.
     *
     * @param compound the data which represents the data
     * which is to be saved
     */
    public void write(Tag.Compound compound) {
        if (this.provider != FlatGeneratorProvider.INSTANCE) {
            compound.putString("TridentProvider", this.provider.getClass().getName());
        }
        compound.putLong("RandomSeed", this.seed);
        compound.putString("generatorName", this.levelType.toString());
        compound.putString("generatorOptions", this.optionString);
        compound.putByte("MapFeatures", (byte) (this.allowFeatures ? 1 : 0));
    }
}