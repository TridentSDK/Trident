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

package net.tridentsdk.server.entity;

import net.tridentsdk.Position;
import net.tridentsdk.Trident;
import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.util.TridentLogger;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages server entities and provides registration procedures
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public final class EntityHandler {
    private static final Map<UUID, Entity> entities = new ConcurrentHashMap<>();

    @InternalUseOnly
    private EntityHandler() {
        if (!Trident.isTrident())
            TridentLogger.error(new IllegalAccessException("EntityManager can only be initalized by TridentSDK!"));
    }

    /**
     * Constructs the EntityManager for use by the server ONLY
     *
     * <p>In other words, DON'T USE IT</p>
     */
    public static EntityHandler create() {
        return new EntityHandler();
    }

    /**
     * Starts entity management and tracks the entity
     *
     * @param entity the entity to manage
     */
    public void register(Entity entity) {
        entities.put(entity.getUniqueId(), entity);
        if (entity instanceof TridentPlayer)
            return;
        // tracker.track(entity);
    }

    /**
     * Removes the entity from management
     *
     * @param entity the entity to remove
     */
    public void removeEntity(Entity entity) {
        entities.remove(entity.getEntityId());
    }

    /**
     * Tracks the movement of the entity, not for teleportation
     *
     * @param entity the entity to track
     * @param from   the original location
     * @param to     the new location
     */
    public void trackMovement(Entity entity, Position from, Position to) {
    }

    /**
     * Gets the entity with the given ID
     *
     * @param id the ID to find the entity by
     * @return the entity with the ID specified
     */
    public Entity entityBy(UUID id) {
        return entities.get(id);
    }

    /**
     * Gets all entities with the given type class
     *
     * @param type the type to search for entities
     * @param <T>  the entity type
     * @return the list of entities with the specified type
     */
    public <T extends Entity> Iterator<Entity> entities(final Class<T> type) {
        return entities.values().stream()
                .filter((e) -> type.getClass().equals(e.getClass()))
                .iterator();
    }
}
