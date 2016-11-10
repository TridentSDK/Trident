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
package net.tridentsdk.server.world.gen;

import net.tridentsdk.server.concurrent.PoolSpec;
import net.tridentsdk.server.concurrent.ServerThreadPool;
import net.tridentsdk.world.gen.GenContainer;

import javax.annotation.concurrent.Immutable;

/**
 * The implementation for a generator container, which is
 * used by world generators to offload generation to
 * different threads.
 */
@Immutable
public final class GenContainerImpl {
    // TODO check this shit out
    private static final ArbitraryRunner ARBITRARY_RUNNER = new ArbitraryRunner();
    private static final DefaultRunner DEFAULT_RUNNER = new DefaultRunner();

    /**
     * The container which is used to run the generator
     * methods
     */
    private final GenContainer runner;

    /**
     * Creates a new generator container implementation
     * which uses the given runner to execute the
     * generation
     * task.
     *
     * @param runner the generation task runner
     */
    private GenContainerImpl(GenContainer runner) {
        this.runner = runner;
    }

    /**
     * Creates a new generator container implementation
     * from the container that is specified in the
     * generator provider which is to be run.
     *
     * @param container the container that is the runner of
     * the generation task
     * @return the new generator implementation of that
     * runner
     */
    public static GenContainerImpl of(GenContainer container) {
        if (container == GenContainer.ARBITRARY) {
            return new GenContainerImpl(ARBITRARY_RUNNER);
        } else if (container == GenContainer.DEFAULT) {
            return new GenContainerImpl(DEFAULT_RUNNER);
        } else {
            return new GenContainerImpl(container);
        }
    }

    /**
     * Implementation of an arbitrary thread runner.
     */
    private static class ArbitraryRunner implements GenContainer {
        private static final ServerThreadPool POOL = ServerThreadPool.forSpec(PoolSpec.CHUNKS);

        @Override
        public void run(Runnable run) {
            POOL.execute(run);
        }
    }

    /**
     * Implementation of a default thread runner, which
     * ensures that plugins that have not accounted for
     * data consistency.
     */
    private static class DefaultRunner implements GenContainer {
        private static final ServerThreadPool POOL = ServerThreadPool.forSpec(PoolSpec.PLUGINS);

        @Override
        public void run(Runnable run) {
            POOL.execute(run);
        }
    }
}