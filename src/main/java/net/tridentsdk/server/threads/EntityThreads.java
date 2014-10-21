/*
 *     Trident - A Multithreaded Server Alternative
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.server.threads;

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
     *
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
                THREAD_MAP.assign(executor, entity);

                return executor;
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
