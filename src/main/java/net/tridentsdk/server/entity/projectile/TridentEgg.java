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
import net.tridentsdk.entity.EntityProperties;
import net.tridentsdk.entity.EntityType;
import net.tridentsdk.entity.living.ProjectileLauncher;
import net.tridentsdk.entity.projectile.Egg;
import net.tridentsdk.server.entity.TridentProjectile;
import net.tridentsdk.server.entity.living.TridentChicken;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents an egg after being thrown
 *
 * @author The TridentSDK Team
 */
public class TridentEgg extends TridentProjectile implements Egg {
    /**
     * Inherits constructor from {@link net.tridentsdk.server.entity.TridentProjectile}
     */
    public TridentEgg(UUID uniqueId, Position spawnLocation, ProjectileLauncher source) {
        super(uniqueId, spawnLocation, source);
    }

    @Override
    public void applyProperties(EntityProperties properties) {
    }

    @Override
    protected void hit() {
        int chance = ThreadLocalRandom.current().nextInt(1, 256);

        if (chance <= 8) {
            if (chance == 1) {
                for (int i = 0; i < 4; i++) {
                    HANDLER.register(new TridentChicken(UUID.randomUUID(), this.location()));
                }
            } else {
                HANDLER.register(new TridentChicken(UUID.randomUUID(), this.location()));
            }
        }
    }

    @Override
    public EntityType type() {
        return EntityType.EGG;
    }
}
