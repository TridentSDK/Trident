/*
 * Copyright (c) 2014, TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of TridentSDK nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
     * @param <T> the entity type
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
