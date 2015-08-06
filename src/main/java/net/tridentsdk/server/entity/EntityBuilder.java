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

import net.tridentsdk.base.Position;
import net.tridentsdk.concurrent.SelectableThreadPool;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.registry.Registered;
import net.tridentsdk.server.concurrent.ThreadsHandler;
import net.tridentsdk.util.TridentLogger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

/**
 * Builds an entity from initializer components and auto-spawns safely
 *
 * <p>This is not thread safe. Do not share across methods, and you should be good.</p>
 *
 * @author The TridentSDK Team
 */
public final class EntityBuilder {
    private UUID uuid = UUID.randomUUID();
    private Position spawn = Position.create(Registered.worlds().get("world"), 0, 0, 0);
    private SelectableThreadPool executor;
    private boolean god;
    private Entity passenger;
    private String displayName;
    private boolean silent;

    private EntityBuilder() {
    }

    public static EntityBuilder create() {
        return new EntityBuilder();
    }

    public EntityBuilder uuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public EntityBuilder spawn(Position spawn) {
        this.spawn = spawn;
        return this;
    }

    public EntityBuilder executor(SelectableThreadPool executor) {
        this.executor = executor;
        return this;
    }

    public EntityBuilder god(boolean god) {
        this.god = god;
        return this;
    }

    public EntityBuilder passenger(Entity passenger) {
        this.passenger = passenger;
        return this;
    }

    public EntityBuilder name(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public EntityBuilder silent(boolean silent) {
        this.silent = silent;
        return this;
    }

    // TODO in reality these should be impl classes??
    public <T extends Entity> T build(Class<T> entityType) {
        TridentEntity entity = null;
        try {
            Constructor<? extends TridentEntity> constructor = (Constructor<? extends TridentEntity>)
                    entityType.getConstructor(UUID.class, Position.class);
            entity = constructor.newInstance(uuid, spawn);
            entity.executor = executor != null ? executor : ThreadsHandler.entityExecutor();
            entity.godMode = god;
            entity.passenger = passenger;
            entity.displayName = displayName;
            entity.nameVisible = displayName != null;
            entity.silent = silent;
            entity.spawn();
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                InstantiationException e) {
            TridentLogger.get().error(e);
        }

        return (T) entity;
    }

    public <T extends Entity> T build(Class<T> entityType, ParameterValue<?>... parameterValues) {
        int paramLen = parameterValues.length;
        Class[] params = new Class[paramLen];
        Object[] args = new Object[paramLen];
        for (int i = 0; i < paramLen; i++) {
            ParameterValue<?> value = parameterValues[i];
            params[i] = value.clazz();
            args[i] = value.value();
        }

        TridentEntity entity = null;
        try {
            Constructor<? extends TridentEntity> constructor = (Constructor<? extends TridentEntity>)
                    entityType.getDeclaredConstructor(params);
            constructor.setAccessible(true);
            entity = constructor.newInstance(args);
            entity.executor = executor != null ? executor : ThreadsHandler.entityExecutor();
            entity.godMode = god;
            entity.passenger = passenger;
            entity.displayName = displayName;
            entity.nameVisible = displayName != null;
            entity.silent = silent;
            entity.spawn();
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                InstantiationException e) {
            TridentLogger.get().error(e);
        }

        return (T) entity;
    }

    /**
     * Immutable parameter type and object value for dynamic constructor resolvation
     *
     * @param <T> the type for the parameter
     * @author The TridentSDK Team
     */
    public static class ParameterValue<T> {
        private final Class<T> c;
        private final T value;

        private ParameterValue(Class<T> c, T value) {
            this.c = c;
            this.value = value;
        }

        /**
         * Creates a new parameter value
         *
         * @param c     the class type
         * @param value the value of the parameter
         * @param <T>   the type
         * @return the new parameter value
         */
        public static <T> ParameterValue from(Class<T> c, T value) {
            return new ParameterValue<>(c, value);
        }

        /**
         * The class type for this parameter
         *
         * @return the parameter class type
         */
        public Class<T> clazz() {
            return this.c;
        }

        /**
         * The argument to be passed in for the parameter
         *
         * @return the value passed for the parameter
         */
        public T value() {
            return this.value;
        }
    }
}