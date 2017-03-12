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
package net.tridentsdk.server.world.gen;

import net.tridentsdk.world.World;
import net.tridentsdk.world.gen.*;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.Set;

/**
 * A generator provider that provides the proper generators
 * for a flat world level type.
 */
@Immutable
public class FlatGeneratorProvider implements GeneratorProvider {
    /**
     * The singleton instance of this provider
     */
    public static final FlatGeneratorProvider INSTANCE = new FlatGeneratorProvider();

    @Override
    public TerrainGenerator getTerrainGenerator(World world) {
        return FlatTerrainGenerator.INSTANCE;
    }

    @Nonnull
    @Override
    public Set<FeatureGenerator> getFeatureGenerators(World world) {
        return Collections.emptySet();
    }

    @Nonnull
    @Override
    public Set<PropGenerator> getPropGenerators(World world) {
        return Collections.emptySet();
    }

    @Override
    public GenContainer getGenerationContainer() {
        return GenContainer.ARBITRARY;
    }
}
