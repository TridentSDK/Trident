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
package net.tridentsdk.server.chunk;

import net.tridentsdk.server.world.TridentChunk;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.atomic.LongAdder;

@ThreadSafe
// TODO add reference to avoid lookup each call
public class CRefCounter {
    private final TridentChunk wrapped;
    private final LongAdder strongRefs = new LongAdder();
    private final LongAdder weakRefs = new LongAdder();

    private CRefCounter(TridentChunk wrapped) {
        this.wrapped = wrapped;
    }

    public static CRefCounter wrap(TridentChunk chunk) {
        return new CRefCounter(chunk);
    }

    public void refStrong() {
        strongRefs.increment();
    }

    public void releaseStrong() {
        strongRefs.decrement();
        wrapped.world().chunkHandler().tryRemove(wrapped.location());

        if (strongRefs.sum() < 0L) throw new IllegalStateException("Sub-zero strongrefs");
    }

    public void refWeak() {
        weakRefs.increment();
    }

    public void releaseWeak() {
        weakRefs.decrement();

        if (weakRefs.sum() < 0L) throw new IllegalStateException("Sub-zero weakrefs");
    }

    public boolean hasStrongRefs() {
        return strongRefs.sum() > 0L;
    }

    public boolean hasWeakRefs() {
        return weakRefs.sum() > 0L;
    }

    public TridentChunk unwrap() {
        return wrapped;
    }

    public long list() {
        return strongRefs.sum();
    }
}