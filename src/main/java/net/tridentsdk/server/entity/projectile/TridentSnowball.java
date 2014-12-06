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
package net.tridentsdk.server.entity.projectile;

import net.tridentsdk.Location;
import net.tridentsdk.entity.EntityProperties;
import net.tridentsdk.entity.decorate.LivingEntity;
import net.tridentsdk.entity.living.Blaze;
import net.tridentsdk.entity.living.EnderDragon;
import net.tridentsdk.entity.living.ProjectileSource;
import net.tridentsdk.server.entity.TridentProjectile;

import java.util.UUID;

/**
 * Represents a snowball which is thrown
 *
 * @author The TridentSDK Team
 */
public class TridentSnowball extends TridentProjectile {
    /**
     * Inherits from {@link net.tridentsdk.server.entity.TridentProjectile}
     */
    public TridentSnowball(UUID uniqueId, Location spawnLocation, ProjectileSource source) {
        super(uniqueId, spawnLocation, source);
    }

    @Override
    public void applyProperties(EntityProperties properties) {
    }

    @Override
    protected void hit() {
        if (this.impaled != null && this.impaled.isImpaledEntity()) {
            if (this.impaled.impaledEntity() instanceof LivingEntity) {
                LivingEntity ent = (LivingEntity) this.impaled.impaledEntity();

                if (ent instanceof Blaze) {
                    // Damage 3 hearts
                } else if (ent instanceof EnderDragon) {
                    // Damage 1 heart
                } else {
                    // Damage 0 hearts (knockback)
                }
            }
        } else {
            this.remove();
        }
    }
}
