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
package net.tridentsdk.server.entity;

import net.tridentsdk.base.Position;
import net.tridentsdk.meta.nbt.CompoundTag;
import net.tridentsdk.meta.nbt.IntTag;

import java.util.UUID;

public abstract class TridentBreedable extends TridentAgeable {
    protected volatile int loveTimeout;
    protected volatile boolean inLove;
    protected volatile boolean canBreed = false;

    public TridentBreedable(UUID id, Position spawnLocation) {
        super(id, spawnLocation);
    }

    @Override
    public boolean canBreed() {
        return canBreed;
    }

    @Override
    public boolean isInLove() {
        return inLove;
    }

    @Override
    public void doLoad(CompoundTag tag) {
        this.loveTimeout = ((IntTag) tag.getTag("InLove")).value();
        this.inLove = false;
    }
}
