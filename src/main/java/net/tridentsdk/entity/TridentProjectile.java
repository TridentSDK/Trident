/*
 *     Trident - A Multithreaded Server Alternative
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
