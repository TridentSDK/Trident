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

import net.tridentsdk.api.Trident;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class TridentRunnable implements Runnable {
    private static int currentId = 0;

    /**
     * Id of this runnable, unique to this runtime
     */
    private final AtomicInteger id = new AtomicInteger(-1);

    private final AtomicLong interval = new AtomicLong();
    private final boolean inAHurry = false;

    public TridentRunnable() {
        id.set(currentId += 1);
    }

    /**
     * Returns if this runnable is in a hurry, usually indicating that the impl is shutting down
     */
    public boolean isInAHurry() {
        return this.inAHurry;
    }

    /**
     * Guaranteed to be run before this Runnable on the main thread, even if this runnable is going to be run
     * asynchronously, useful for collecting resources to work on.
     */
    public void prerunSync() {
    }

    /**
     * Runs after this runnable has finished, asynchronously
     */
    public void runAfterAsync() {
    }

    /**
     * Runs after this runnable has finished, synchronously
     */
    public void runAfterSync() {
    }

    public final void cancel() {
        Trident.getServer().getScheduler().cancel(this);
    }

    /**
     * Gets how long between runs this is supposed to wait if it is a repeating task
     */
    public final long getInterval() {
        return Trident.getScheduler().wrapperByRun(this).getInterval();
    }

    /**
     * Sets how long this runnable should wait between executions if this is a repeating task <p>If this task is
     * synchronous to the main thread, the change will be immediate, if it is not, the change may take an iteration to
     * take effect, however {@link TridentRunnable#getInterval()} will reflect the changes immediately</p>
     */
    public final void setInterval(long interval) {
        Trident.getScheduler().wrapperByRun(this).setInterval(interval);
    }

    /**
     * Used internally to refer to this runnable, probably shouldn't be used by plugins
     */
    public final int getId() {
        return this.id.get();
    }

    /**
     * Should be reimplemented by a runnable if it wants to use in a hurry <p>Reimplement to return true</p>
     */
    public boolean usesInAHurry() {
        return false;
    }
}
