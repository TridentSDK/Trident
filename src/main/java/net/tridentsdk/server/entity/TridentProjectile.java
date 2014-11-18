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
package net.tridentsdk.server.entity;

import net.tridentsdk.Location;
import net.tridentsdk.entity.EntityProperties;
import net.tridentsdk.entity.Impalable;
import net.tridentsdk.entity.Projectile;
import net.tridentsdk.entity.living.ProjectileSource;

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
    protected volatile WeakReference<ProjectileSource> source;
    /**
     * The impalable that the projectile hit, if any
     */
    protected Impalable impaled;

    /**
     * Inherits UUID and spawnLocation from {@link TridentEntity}
     *
     * @param source the entity which fired the projectile
     */
    public TridentProjectile(UUID uniqueId, Location spawnLocation, ProjectileSource source) {
        super(uniqueId, spawnLocation);
        this.source = new WeakReference<>(source);
    }

    @Override
    public boolean isNameVisible() {
        return false;
    }

    @Override
    public abstract void applyProperties(EntityProperties properties);

    @Override
    public void doHit() {
        // TODO Perform impaling logic
        Impalable impalable = null;
        impalable.put(this);
        this.hit();
    }

    /**
     * Performed when the projectile hits something
     */
    protected abstract void hit();

    @Override
    public Impalable getImpaled() {
        return this.impaled;
    }

    @Override
    public void setSource(final ProjectileSource shooter) {
        super.executor.addTask(new Runnable() {
            @Override
            public void run() {
                TridentProjectile.this.source = new WeakReference<>(shooter);
            }
        });
    }

    @Override
    public ProjectileSource getProjectileSource() {
        return this.source.get();
    }
}
