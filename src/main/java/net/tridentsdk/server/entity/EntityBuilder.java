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
import net.tridentsdk.entity.Entity;
import net.tridentsdk.factory.ExecutorFactory;
import net.tridentsdk.meta.nbt.CompoundTag;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.threads.ThreadsHandler;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.world.World;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Builds an entity from initializer components and auto-spawns safely
 *
 * <p>This is not thread safe. Do not share across methods, and you should be good.</p>
 *
 * @author The TridentSDK Team
 */
public final class EntityBuilder {
    private UUID uuid = UUID.randomUUID();
    private Position spawn = Position.create(new Callable<World>() {
        @Override
        public World call() {
            for (World world : Trident.worlds().values()) {
                if (world.name().equals("world"))
                    return world;
            }
            return null;
        }
    }.call(), 0, 0, 0);
    private ExecutorFactory<Entity> executor;
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

    public EntityBuilder executor(ExecutorFactory<? extends Entity> executor) {
        this.executor = (ExecutorFactory<Entity>) executor;
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
            TridentLogger.error(e);
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
        if(entityType == TridentPlayer.class) {
            entity = new TridentPlayer((CompoundTag)parameterValues[0].value(),
                    (TridentWorld)parameterValues[1].value(),
                    (ClientConnection)parameterValues[2].value());
            entity.executor = executor != null ? executor : ThreadsHandler.entityExecutor();
            entity.godMode = god;
            entity.passenger = passenger;
            entity.displayName = displayName;
            entity.nameVisible = displayName != null;
            entity.silent = silent;
            entity.spawn();
            return (T) entity;
        }
        try {
            Constructor<? extends TridentEntity> constructor = (Constructor<? extends TridentEntity>)
                    entityType.getConstructor(params);
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
            TridentLogger.error(e);
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