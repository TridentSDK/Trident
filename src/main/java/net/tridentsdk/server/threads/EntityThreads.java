/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.server.threads;

import net.tridentsdk.api.entity.Entity;

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
    static final ConcurrentCache<Entity, ConcurrentTaskExecutor.TaskExecutor> CACHE_MAP = new ConcurrentCache<>();

    static final ExecutorService SERVICE = Executors.newSingleThreadExecutor();

    private EntityThreads() {
    }

    /**
     * Gets the management tool for the entity <p/> <p>This will put in a new value for the caches if cannot find for a
     * new entity</p> <p/> <p>May block the first call</p>
     *
     * @param entity the entity to retrieve the thread handler for
     * @return the task execution handler for the entity
     */
    public static ConcurrentTaskExecutor.TaskExecutor entityThreadHandle(final Entity entity) {
        return EntityThreads.CACHE_MAP.retrieve(entity, new Callable<ConcurrentTaskExecutor.TaskExecutor>() {
            @Override
            public ConcurrentTaskExecutor.TaskExecutor call() throws Exception {
                ConcurrentTaskExecutor.TaskExecutor executor = EntityThreads.THREAD_MAP.getScaledThread();
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
