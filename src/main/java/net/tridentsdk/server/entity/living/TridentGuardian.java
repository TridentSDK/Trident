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
package net.tridentsdk.server.entity.living;

import net.tridentsdk.Position;
import net.tridentsdk.entity.living.Guardian;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.event.entity.EntityDamageEvent;
import net.tridentsdk.server.entity.TridentLivingEntity;

import java.util.UUID;

/**
 * Represents a guardian entity
 *
 * @author The TridentSDK Team
 */
public class TridentGuardian extends TridentLivingEntity implements Guardian {
    public TridentGuardian(UUID uuid, Position spawnPosition) {
        super(uuid, spawnPosition);
    }

    @Override
    public EntityDamageEvent getLastDamageEvent() {
        return null;
    }

    @Override
    public Player getLastPlayerDamager() {
        return null;
    }

    @Override
    public boolean isElder() {
        return false;
    }

    @Override
    public EntityType getType() {
        return EntityType.GUARDIAN;
    }
}
