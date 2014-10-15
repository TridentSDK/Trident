/*
 *     Trident - A Multithreaded Server Alternative
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

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import net.tridentsdk.api.Trident;
import net.tridentsdk.api.entity.Entity;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class EntityManager {
    private final Map<Integer, Entity> entities = new ConcurrentHashMap<>();

    /**
     * Constructs the EntityManager for use by the server ONLY
     *
     * <p>In other words, DON'T USE IT</p>
     */
    public EntityManager() {
        if (!Trident.isTrident())
            throw new UnsupportedOperationException("EntityManager can only be initalized by TridentSDK!");
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
