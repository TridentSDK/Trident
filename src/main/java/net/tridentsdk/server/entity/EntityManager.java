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

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import net.tridentsdk.Trident;
import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.util.TridentLogger;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages server entities and provides registration procedures
 *
 * @author The TridentSDK Team
 */
public final class EntityManager {
    private final Map<Integer, Entity> entities = new ConcurrentHashMap<>();

    /**
     * Constructs the EntityManager for use by the server ONLY
     * <p/>
     * <p>In other words, DON'T USE IT</p>
     */
    @InternalUseOnly
    public EntityManager() {
        if (!Trident.isTrident())
            TridentLogger.error(new UnsupportedOperationException("EntityManager can only be initalized by TridentSDK!"));
    }

    void registerEntity(Entity entity) {
        this.entities.put(entity.getId(), entity);
    }

    /**
     * Gets the entity with the given ID
     *
     * @param id the ID to find the entity by
     * @return the entity with the ID specified
     */
    public Entity getEntity(int id) {
        return this.entities.get(id);
    }

    /**
     * Gets all entities with the given type class
     *
     * @param type the type to search for entities
     * @param <T>  the entity type
     * @return the list of entities with the specified type
     */
    public <T> ArrayList<T> getEntities(final Class<T> type) {
        Predicate<Entity> pred = new Predicate<Entity>() {
            @Override
            public boolean apply(Entity e) {
                return Predicates.assignableFrom(type.getClass()).apply(e.getClass());
            }
        };

        return (ArrayList<T>) Lists.newArrayList(Iterators.filter(this.entities.values().iterator(), pred));
    }
}
