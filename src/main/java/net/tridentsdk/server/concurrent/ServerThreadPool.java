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
package net.tridentsdk.server.concurrent;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Managed set of threads that can be constrained
 * in CPU resources and performs work stealing when
 * necessary.
 */
@Immutable
public class ServerThreadPool implements Executor {
    /**
     * Mapping of spec objects to delegate thread pools.
     */
    private static final Map<PoolSpec, ServerThreadPool> pools =
            Maps.newConcurrentMap();
    /**
     * Delegate executor service that is determined via
     * spec in the {@link #forSpec(PoolSpec)} method.
     */
    private final ExecutorService delegate;

    /**
     * Invoked from the factory method to create a new
     * delegated thread pool.
     *
     * @param executor the delegate
     */
    private ServerThreadPool(ExecutorService executor) {
        this.delegate = executor;
    }

    /**
     * Initializer for server startup
     */
    public static void init() {
        forSpec(PoolSpec.WORLDS);
        forSpec(PoolSpec.CHUNKS);
        forSpec(PoolSpec.ENTITIES);
        forSpec(PoolSpec.PLAYERS);
        forSpec(PoolSpec.PLUGINS);
        forSpec(PoolSpec.SCHEDULER);
    }

    /**
     * Creates a new thread pool for the given spec.
     *
     * @param spec the specification for the new thread
     *             pool.
     * @return the thread pool that is based on the spec
     */
    public static ServerThreadPool forSpec(PoolSpec spec) {
        return pools.computeIfAbsent(spec, k -> {
            int config = spec.getMaxThreads();
            if (spec.isDoStealing()) {
                return new ServerThreadPool(Executors.newWorkStealingPool(config));
            } else {
                return new ServerThreadPool(new ThreadPoolExecutor(config, config,
                        60L, TimeUnit.SECONDS,
                        Queues.newLinkedBlockingQueue(),
                        spec));
            }
        });
    }

    /**
     * Attempts to shutdown every thread pool that has been
     * registered through a spec in the server.
     */
    public static void shutdownAll() {
        for (ServerThreadPool pool : pools.values()) {
            pool.shutdown();
        }
    }

    /**
     * Attempts to shutdown the thread pool immediately.
     */
    public void shutdown() {
        this.delegate.shutdownNow();
    }

    public <T> Future<T> submit(Callable<T> task) {
        return this.delegate.submit(task);
    }

    public <T> Future<T> submit(Runnable task, T result) {
        return this.delegate.submit(task, result);
    }

    public Future<?> submit(Runnable task) {
        return this.delegate.submit(task);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return this.delegate.invokeAll(tasks);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate.invokeAll(tasks);
    }

    /**
     * Executes the given runnable command in the thread
     * pool.
     *
     * @param command the command which to schedule for
     *                running.
     */
    @Override
    public void execute(@Nonnull Runnable command) {
        this.delegate.execute(command);
    }
}