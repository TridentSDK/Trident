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

import net.tridentsdk.Position;
import net.tridentsdk.entity.EntityProperties;
import net.tridentsdk.entity.Projectile;
import net.tridentsdk.entity.living.ProjectileLauncher;

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
    protected volatile WeakReference<ProjectileLauncher> source;

    /**
     * Inherits UUID and spawnLocation from {@link TridentEntity}
     *
     * @param source the entity which fired the projectile
     */
    public TridentProjectile(UUID uniqueId, Position spawnLocation, ProjectileLauncher source) {
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
        this.hit();
    }

    /**
     * Performed when the projectile hits something
     */
    protected abstract void hit();

    @Override
    public ProjectileLauncher launcher() {
        return this.source.get();
    }

    @Override
    public void setLauncher(final ProjectileLauncher shooter) {
        TridentProjectile.this.source = new WeakReference<>(shooter);
    }
}
