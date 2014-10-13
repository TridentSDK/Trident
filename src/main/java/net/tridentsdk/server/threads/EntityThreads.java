/*
 * Copyright (c) 2014, TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of TridentSDK nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.server.threads;

import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.threads.TaskExecutor;

import java.util.concurrent.*;

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
        return EntityThreads.CACHE_MAP.retrieve(entity, new Callable<TaskExecutor>() {
            @Override
            public TaskExecutor call() throws Exception {
                TaskExecutor executor = EntityThreads.THREAD_MAP.getScaledThread();
                EntityThreads.THREAD_MAP.assign(executor, entity);

                return executor;
            }
        }, EntityThreads.SERVICE);
    }

    /**
     * Decaches the entity handler from the mappings
     *
     * @param entity the entity to decache
     */
    public static void remove(Entity entity) {
        EntityThreads.THREAD_MAP.removeAssignment(entity);
        EntityThreads.CACHE_MAP.remove(entity);
    }
}
