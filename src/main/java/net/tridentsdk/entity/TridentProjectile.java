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

import net.tridentsdk.api.Block;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.entity.EntityProperties;
import net.tridentsdk.api.entity.Projectile;
import net.tridentsdk.api.entity.living.ProjectileSource;

import java.lang.ref.WeakReference;
import java.util.UUID;

/**
 * Represents an entity that is thrown or launched
 *
 * @author The TridentSDK Team
 */
public abstract class TridentProjectile extends TridentEntity implements Projectile {
    /**
     * The source that fires the projectile
     */
    protected final WeakReference<ProjectileSource> source;
    /**
     * The entity that the projectile hit, if any
     */
    protected Entity entityHit;

    protected boolean bounce;

    /**
     * Inherits UUID and spawnLocation from {@link net.tridentsdk.entity.TridentEntity}
     *
     * @param source the entity which fired the projectile
     */
    public TridentProjectile(UUID uniqueId, Location spawnLocation, ProjectileSource source, boolean bouncy) {
        super(uniqueId, spawnLocation);
        this.source = new WeakReference<>(source);
        this.bounce = bouncy;
    }

    @Override
    public boolean isNameVisible() {
        return false;
    }

    @Override
    public abstract void applyProperties(EntityProperties properties);


    @Override public ProjectileSource getProjectileSource() {
        return this.source.get();
    }

    @Override
    public ProjectileSource getShooter() {
        return this.source.get();
    }

    @Override
    public boolean doesBounce() {
        return this.bounce;
    }

    @Override
    public void setBounce(boolean bouncy) {
        this.bounce = bouncy;
    }

    @Override
    public Block getCurrentTile() {
        return this.loc.getWorld().getBlockAt(this.loc);
    }

    /**
     * Performed when the projectile hits something
     */
    protected abstract void hit();
}
