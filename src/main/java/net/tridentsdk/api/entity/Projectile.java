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
package net.tridentsdk.api.entity;

import net.tridentsdk.api.entity.living.ProjectileSource;

/**
 * Represents a Projectile
 *
 * @author TridentSDK Team
 */
public interface Projectile extends Entity {
    /**
     * Performs hit action
     */
    void doHit();

    /**
     * Returns the block/entity that was impaled by the projectile
     *
     * @return the impaled object by the projectile
     */
    Impalable getImpaled();

    /**
     * Returns the shooter of the Projectile
     *
     * @param shooter the ProjectileSource of the Projectile
     */
    void setSource(ProjectileSource shooter);

    /**
     * The projectile source
     *
     * @return gets the source of the projectile
     */
    ProjectileSource getProjectileSource();
}
