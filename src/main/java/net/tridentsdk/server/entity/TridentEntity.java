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
package net.tridentsdk.server.entity;

import net.tridentsdk.base.Position;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.world.World;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The implementation class for an entity.
 */
public abstract class TridentEntity implements Entity {
    /**
     * The counter which produces the entity ID numbers
     */
    private static final AtomicInteger EID_COUNTER = new AtomicInteger();

    /**
     * The ID number assigned to this entity
     */
    private final int id;
    /**
     * The position at which this entity is located
     */
    private volatile Position position;

    public TridentEntity(World world) {
        this.id = EID_COUNTER.incrementAndGet();
        this.position = new Position(world);
    }

    @Override
    public int id() {
        return this.id;
    }

    @Override
    public Position position() {
        return this.position;
    }

    @Override
    public TridentWorld world() {
        return (TridentWorld) this.position.world();
    }

    @Override
    public final void remove() {
        this.doRemove();
    }

    /**
     * Ticks the entity.
     */
    public final void tick() {
        this.doTick();
    }

    /**
     * Removal hook.
     */
    public abstract void doRemove();

    /**
     * Ticking hook.
     */
    public abstract void doTick();
}
