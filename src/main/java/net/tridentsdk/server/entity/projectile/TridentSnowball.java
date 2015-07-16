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

import net.tridentsdk.Position;
import net.tridentsdk.entity.traits.EntityProperties;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.entity.LivingEntity;
import net.tridentsdk.entity.living.Blaze;
import net.tridentsdk.entity.living.EnderDragon;
import net.tridentsdk.entity.living.ProjectileLauncher;

import java.util.UUID;

/**
 * Represents a snowball which is thrown
 *
 * @author The TridentSDK Team
 */
public class TridentSnowball extends TridentProjectile {
    /**
     * Inherits from {@link TridentProjectile}
     */
    public TridentSnowball(UUID uniqueId, Position spawnLocation, ProjectileLauncher source) {
        super(uniqueId, spawnLocation, source);
    }

    @Override
    public void applyProperties(EntityProperties properties) {
    }

    @Override
    protected void hit() {
        // TODO
        LivingEntity ent = null;
        if (ent instanceof Blaze) {
            // Damage 3 hearts
        } else if (ent instanceof EnderDragon) {
            // Damage 1 heart
        } else {
            // Damage 0 hearts (knockback)
        }
    }

    @Override
    public EntityType getType() {
        return EntityType.SNOWBALL;
    }
    
}
