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

import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Managed set of threads that can be constrained
 * in CPU resources and performs work stealing when
 * necessary.
 */
public class ServerThreadPool {
    private final Executor delegate;

    private ServerThreadPool(ThreadBlockSpec spec) {
        this.delegate = null;
    }

    private static final Map<ThreadBlockSpec, ServerThreadPool> POOLS =
            Maps.newConcurrentMap();

    public static ServerThreadPool forSpec(ThreadBlockSpec spec) {
        return null;
    }
}