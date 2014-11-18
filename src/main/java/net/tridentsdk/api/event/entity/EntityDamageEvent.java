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
package net.tridentsdk.api.event.entity;

import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.entity.LivingEntity;

public class EntityDamageEvent extends EntityEvent {
    private final Cause cause;
    private boolean cancel;
    private double damage;

    /**
     * @param entity the entity associated with this event
     * @param damage the amount of damage dealt to the player
     */

    public EntityDamageEvent(Entity entity, double damage, Cause cause) {
        super(entity);
        this.setDamage(damage);
        this.cause = cause;
    }

    public Cause getCause() {
        return this.cause;
    }

    /**
     * @return return the amount of damage dealt
     */

    public double getDamage() {
        return this.damage;
    }

    /**
     * Change the damage value dealt
     *
     * @param damage the amount of damage dealt
     */

    public void setDamage(double damage) {
        this.damage = damage;
    }

    @Override
    public LivingEntity getEntity() {
        return (LivingEntity) super.getEntity();
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public enum Cause {
        STARVATION,
        FIRE,
        FALL,
        EXPLOSION,
        HIT,
        ENDER_PEARL,
        PROJECTILE,
        LIGHTNING,
        DROWNING,
        SUFFOCATION,
        ANVIL,
        CONTACT,
        LAVA,
        POISON,
        WITHER,
        VOID
    }
}
