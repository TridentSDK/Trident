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

package net.tridentsdk.entity;

import com.google.common.util.concurrent.AtomicDouble;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.entity.LivingEntity;
import net.tridentsdk.api.util.Vector;

import java.util.UUID;

public abstract class TridentLivingEntity extends TridentEntity implements LivingEntity {

    protected final boolean dead;
    protected final boolean canPickup = true;
    protected final AtomicDouble health = new AtomicDouble(0.0);
    protected double maxHealth;
    protected volatile AtomicDouble fireTicks = new AtomicDouble(0.0);
    protected volatile AtomicDouble airTicks = new AtomicDouble(0.0);

    public TridentLivingEntity(UUID id, Location spawnLocation) {
        super(id, spawnLocation);

        this.dead = false;
    }

    @Override
    public double getHealth() {
        return this.health.get();
    }

    @Override
    public void setHealth(double health) {
        this.health.set(health);
    }

    @Override
    public double getMaxHealth() {
        return this.maxHealth;
    }

    @Override
    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }

    @Override
    public Location getEyeLocation() {
        return this.getLocation().getRelative(new Vector(0.0d, 1.0d, 0.0d));
    }

    @Override
    public long getRemainingAir() {
        return (long) this.airTicks.get();
    }

    @Override
    public void setRemainingAir(long ticks) {
        this.airTicks.set((double) ticks);
    }

    @Override
    public boolean canPickupItems() {
        return this.canPickup;
    }

    @Override
    public boolean isDead() {
        return this.dead;
    }
}
