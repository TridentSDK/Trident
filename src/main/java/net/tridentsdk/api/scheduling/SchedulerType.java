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
package net.tridentsdk.api.scheduling;

public enum SchedulerType {
    /**
     * Represents a Scheduler which is Asynchronous, which will run a task immediately.
     */
    ASYNC_RUN,

    /**
     * Represents a Scheduler which is Asynchronous, which will run a task later.
     */
    ASYNC_LATER,

    /**
     * Represents a Scheduler which is Asynchronous, which repeats a task.
     */
    ASYNC_REPEAT,

    /**
     * Represents a Scheduler which is Synchronous, which will run a task immediately.
     */
    SYNC_RUN,

    /**
     * Represents a Scheduler which is Synchronous, which will run a task later.
     */
    SYNC_LATER,

    /**
     * Represents a Scheduler which is Synchronous, which repeats a task.
     */
    SYNC_REPEAT
}
