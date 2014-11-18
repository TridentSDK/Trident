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
package net.tridentsdk.impl.threads;

import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.threads.TaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Entity handling thread manager, there are 2 thread by default
 *
 * @author The TridentSDK Team
 */
public final class EntityThreads {
    static final ConcurrentTaskExecutor<Entity> THREAD_MAP = new ConcurrentTaskExecutor<>(2);
    static final ConcurrentCache<Entity, TaskExecutor> CACHE_MAP = new ConcurrentCache<>();

    static final ExecutorService SERVICE = Executors.newSingleThreadExecutor();

    private EntityThreads() {
    }

    /**
     * Gets the management tool for the entity <p/> <p>This will put in a new value for the caches if cannot find for a
     * new entity</p>
     * <p/>
     * <p>May block the first call</p>
     *
     * @param entity the entity to retrieve the thread handler for
     * @return the task execution handler for the entity
     */
    public static TaskExecutor entityThreadHandle(final Entity entity) {
        return CACHE_MAP.retrieve(entity, new Callable<TaskExecutor>() {
            @Override
            public TaskExecutor call() throws Exception {
                TaskExecutor executor = THREAD_MAP.getScaledThread();
                return THREAD_MAP.assign(executor, entity);
            }
        }, SERVICE);
    }

    /**
     * Decaches the entity handler from the mappings
     *
     * @param entity the entity to decache
     */
    public static void remove(Entity entity) {
        THREAD_MAP.removeAssignment(entity);
        CACHE_MAP.remove(entity);
    }
}
