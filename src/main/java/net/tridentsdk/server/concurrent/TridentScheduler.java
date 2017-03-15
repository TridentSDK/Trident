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
package net.tridentsdk.server.concurrent;

import lombok.Getter;

import javax.annotation.concurrent.Immutable;

/**
 * Implementation of a scheduler which allows plugins and
 * the server to ask for tasks to be done in other threads
 * or with delays.
 */
@Immutable
public final class TridentScheduler {
    private static final ServerThreadPool POOL = ServerThreadPool.forSpec(PoolSpec.SCHEDULER);

    @Getter
    private static final TridentScheduler instance = new TridentScheduler();

    private TridentScheduler() {
    }

    public void tick() {
    }
}