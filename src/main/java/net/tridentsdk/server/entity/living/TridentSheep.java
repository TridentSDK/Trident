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

import java.util.UUID;

import net.tridentsdk.Position;
import net.tridentsdk.base.SubstanceColor;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.living.Sheep;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.event.entity.EntityDamageEvent;
import net.tridentsdk.server.entity.TridentBreedable;

/**
 * Represents a sheep
 *
 * @author The TridentSDK Team
 */
public class TridentSheep extends TridentBreedable implements Sheep {
	
    public TridentSheep(UUID uuid, Position spawnPosition) {
        super(uuid, spawnPosition);
    }

    @Override
    public SubstanceColor getColor() {
        return null;
    }
    
    @Override
    public void setColor(SubstanceColor color) {
    	
    }

    @Override
    public boolean isShearable() {
        return false;
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
    public EntityType getType() {
        return EntityType.SHEEP;
    }
    
}
