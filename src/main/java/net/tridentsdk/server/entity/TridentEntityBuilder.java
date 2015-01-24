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

import net.tridentsdk.Coordinates;
import net.tridentsdk.Trident;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.EntityBuilder;
import net.tridentsdk.entity.ParameterValue;
import net.tridentsdk.factory.ExecutorFactory;
import net.tridentsdk.server.threads.ThreadsHandler;
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
public final class TridentEntityBuilder extends EntityBuilder {
    private UUID uuid = UUID.randomUUID();
    private Coordinates spawn = Coordinates.create(new Callable<World>() {
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

    private TridentEntityBuilder() {
    }

    public static TridentEntityBuilder create() {
        return new TridentEntityBuilder();
    }

    public TridentEntityBuilder uuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public TridentEntityBuilder spawn(Coordinates spawn) {
        this.spawn = spawn;
        return this;
    }

    public TridentEntityBuilder executor(ExecutorFactory<? extends Entity> executor) {
        this.executor = (ExecutorFactory<Entity>) executor;
        return this;
    }

    public TridentEntityBuilder god(boolean god) {
        this.god = god;
        return this;
    }

    public TridentEntityBuilder passenger(Entity passenger) {
        this.passenger = passenger;
        return this;
    }

    public TridentEntityBuilder name(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public TridentEntityBuilder silent(boolean silent) {
        this.silent = silent;
        return this;
    }

    // TODO in reality these should be impl classes??
    public <T extends Entity> T build(Class<T> entityType) {
        TridentEntity entity = null;
        try {
            Constructor<? extends TridentEntity> constructor = (Constructor<? extends TridentEntity>)
                    entityType.getConstructor(UUID.class, Coordinates.class);
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
}