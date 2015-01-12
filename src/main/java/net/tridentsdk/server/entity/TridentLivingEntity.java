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

import com.google.common.util.concurrent.AtomicDouble;
import net.tridentsdk.Coordinates;
import net.tridentsdk.entity.LivingEntity;
import net.tridentsdk.util.Vector;

import java.util.UUID;

/**
 * An entity that has health
 *
 * @author The TridentSDK Team
 */
public abstract class TridentLivingEntity extends TridentEntity implements LivingEntity {
    /**
     * The entity health
     */
    protected final AtomicDouble health = new AtomicDouble(0.0);
    /**
     * Whether the entity is dead
     */
    protected volatile boolean dead;
    /**
     * Whether the entity can pick up items
     */
    protected volatile boolean canPickup = true;
    /**
     * The maximum available health
     */
    protected volatile double maxHealth;

    /**
     * Inherits from {@link TridentEntity} <p/> <p>The entity is immediately set "non-dead" after {@code super}
     * call</p>
     */
    public TridentLivingEntity(UUID id, Coordinates spawnLocation) {
        super(id, spawnLocation);

        this.dead = false;
    }

    @Override
    public double health() {
        return this.health.get();
    }

    @Override
    public void setHealth(double health) {
        this.health.set(health);
    }

    @Override
    public double maxHealth() {
        return this.maxHealth;
    }

    @Override
    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }

    @Override
    public Coordinates headLocation() {
        return this.location().relative(new Vector(0.0d, 1.0d, 0.0d));
    }

    @Override
    public long remainingAir() {
        return this.airTicks.get();
    }

    @Override
    public void setRemainingAir(long ticks) {
        this.airTicks.set((int) ticks);
    }

    @Override
    public boolean canCollectItems() {
        return this.canPickup;
    }

    @Override
    public boolean isDead() {
        return this.dead;
    }

    @Override
    public void remove() {
        dead = true;
        super.remove();
    }
}
