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
package net.tridentsdk.entity;

import net.tridentsdk.Location;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.living.ProjectileSource;
import net.tridentsdk.event.entity.EntityDamageEvent;

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
