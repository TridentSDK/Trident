/*
 *     TridentSDK - A Minecraft Server API
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
package net.tridentsdk.api.event.entity;

import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.entity.LivingEntity;
import net.tridentsdk.api.entity.living.Player;

public class EntityDeathEvent extends EntityEvent {

    /**
     * @param entity the entity that has died
     */

    public EntityDeathEvent(Entity entity) {
        super(entity);
    }

    public EntityDamageEvent getDeathCause() {
        return this.getEntity().getLastDamageCause();
    }

    /**
     * Returns a Player if a player was involved in the killing of this entity, else null
     */
    public Player killedByPlayer() {
        return this.getEntity().hurtByPlayer();
    }

    /**
     * Returns whether or not a player was involved in the killing of this entity
     */
    public boolean wasKilledByPlayer() {
        return this.killedByPlayer() == null;
    }

    @Override
    public LivingEntity getEntity() {
        return (LivingEntity) super.getEntity();
    }
}


