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

import net.tridentsdk.api.Location;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.entity.living.ProjectileSource;
import net.tridentsdk.api.event.entity.EntityDamageEvent;

public interface LivingEntity extends Entity, ProjectileSource, Impalable {
    /**
     * Makes the specified entity invisible to the current entity
     *
     * @param entity the entity to make invisible to this entity
     */
    void hide(Entity entity);

    /**
     * Un-hides the entity that was hidden from view, or does nothing of already visible
     *
     * @param entity the entity to make visible to this entity
     */
    void show(Entity entity);

    /**
     * Returns the health of the Entity
     *
     * @return double health of the Entity
     */
    double getHealth();

    /**
     * Sets the health of the Entity
     *
     * @param health health of the Entity
     */
    void setHealth(double health);

    /**
     * Returns the maximum health of the Entity
     *
     * @return double maximum health of the Entity
     */
    double getMaxHealth();

    /**
     * Sets the maximum health of the Entity
     * <p/>
     * <p>maxHealth cannot be above the current health of the Entity</p>
     * TODO: Rephrase?
     *
     * @param maxHealth maximum health of the Entity
     */
    void setMaxHealth(double maxHealth);

    /**
     * Returns the amount of remaining air for the Entity
     *
     * @return long remaining amount of air in ticks
     */
    long getRemainingAir();

    /**
     * Sets the amount of remaining air for the LivingAir
     *
     * @param ticks long amount of remaining air in ticks
     */
    void setRemainingAir(long ticks);

    /**
     * Returns the display name for the Entity
     *
     * @return String the display name for the Entity
     */
    @Override
    String getDisplayName();

    /**
     * Returns the location of the Entity's eye
     *
     * @return Location the location of the Entity's eye
     */
    Location getEyeLocation();

    /**
     * Returns if the Entity can pickup items
     *
     * @return true if the Entity can pickup items
     */
    boolean canPickupItems();

    /**
     * Returns the last EntityDamageEvent which inflicted this Entity
     * <p/>
     * <p>The event may be cancelled.</p>
     *
     * @return EntityDamageEvent the last Entity to inflict this Entity
     */
    EntityDamageEvent getLastDamageCause();

    /**
     * Returns the player who dealt damage to this Entity since its last full heal
     * <p>Used for death messages</p>
     *
     * @return Player the player who dealt damage to this entity since last full heal
     * Returns null if no player has damaged the Entity
     */
    Player hurtByPlayer();

    /**
     * Checks if the entity has died, or has 0 health. Should only apply to entities that are "live" (TODO
     * Entity)
     *
     * @return {@code true} if the entity is dead, {@code false} if it is alive
     */
    boolean isDead();
}
