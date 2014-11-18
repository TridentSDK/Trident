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
package net.tridentsdk.api.entity;

import net.tridentsdk.api.Location;
import net.tridentsdk.api.util.Vector;
import net.tridentsdk.api.world.World;

import java.util.List;
import java.util.UUID;

/**
 * Represents the abstraction for a mob, player, animal, or other "object" that is not a block type
 *
 * @author The TridentSDK Team
 */
public interface Entity {
    /**
     * Moves the entity to the specified location
     *
     * @param x the x coordinate of the location
     * @param y the y coordinate of the location
     * @param z the z coordinate of the location
     */
    void teleport(double x, double y, double z);

    /**
     * Moves the current entity to the provided entity's location
     *
     * @param entity the entity to move the current entity to
     */
    void teleport(Entity entity);

    /**
     * Moves the entity to the coordinates specified by the location object passed in
     *
     * @param location the location to move the entity to
     */
    void teleport(Location location);

    /**
     * The world which the entity resides in
     *
     * @return the world that contains the entity
     */
    World getWorld();

    /**
     * The location of the entity with respect to the coordinate grid
     *
     * @return the entity's location
     */
    Location getLocation();

    /**
     * The direction and movement magnitude of the entity
     *
     * @return the vector representing the entity's velocity
     */
    Vector getVelocity();

    /**
     * Sets the entity's movement direction and speed to the magnitude of the vector
     *
     * @param vector the vector to set the entity velocity to
     */
    void setVelocity(Vector vector);

    /**
     * Perform entity management tasks in the 1/20 second impl heartbeat
     */
    void tick();

    /**
     * Checks if the entity is currently on the ground, or at least touching the ground
     *
     * @return {@code true} if the entity touches the ground, {@code false} if it is in the air (such as if it was
     * falling)
     */
    boolean isOnGround();

    /**
     * Gets the entities that are close to this entity
     *
     * @param radius the spherical radius to look for entities around
     * @return the collection of entities within the radius around the entity
     */
    List<Entity> getNearbyEntities(double radius);

    /**
     * Gets the display name for the entity, used on inventories and deaths
     *
     * @return Display name
     */
    String getDisplayName();

    /**
     * Sets the entity's display name, effects inventories (if applicable) and death messages
     *
     * @param name Entity name
     */
    void setDisplayName(String name);

    /**
     * Gets if the entity's display name visible
     *
     * @return if the entity's display name visible
     */
    boolean isNameVisible();

    /**
     * Gets if the entity is silent (sounds)
     *
     * @return if the entity is silent
     */
    boolean isSilent();

    /**
     * The identifier for this entity for runtime, see getUniqueId for a set id of the entity
     *
     * @return the id to all entities on the impl at runtime
     * @see net.tridentsdk.api.entity.Entity#getUniqueId()
     */
    int getId();

    /**
     * The unique id for the entity to the impl
     *
     * @return The unique id for the entity
     */
    UUID getUniqueId();

    /**
     * Removes the entity from the world and destroys it, freeing all memory associated with it
     */
    void remove();

    /**
     * Gets the entity that is riding this entity, if there is any
     *
     * @return the entity that is riding, {@code null} if there are none
     */
    Entity getPassenger();

    /**
     * Mounts the specified entity on this entity
     *
     * @param entity the entity to set passenger to this entity
     */
    void setPassenger(Entity entity);

    /**
     * Removes the mounted entity, if there are any
     */
    void eject();

    /**
     * Gets the type of entity
     *
     * @return the entity type that is represented
     */
    EntityType getType();

    /**
     * Sets the properties of this entity to the specified properties
     *
     * @param properties the properties to set
     */
    void applyProperties(EntityProperties properties);
}
