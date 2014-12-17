package net.tridentsdk.server.entity;

import net.tridentsdk.Coordinates;
import net.tridentsdk.Trident;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.factory.ExecutorFactory;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.world.World;

import javax.annotation.concurrent.NotThreadSafe;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.concurrent.Callable;

@NotThreadSafe // Designed for use in a single method
public final class EntityBuilder {
    private UUID uuid = UUID.randomUUID();
    private Coordinates spawn = new Coordinates(new Callable<World>() {
        @Override
        public World call() {
            for (World world : Trident.getWorlds())
                if (world.getName().equals("world"))
                    return world;
            return null;
        }
    }.call(), 0, 0, 0);
    private ExecutorFactory<TridentEntity> executor;
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

    public EntityBuilder spawnLocation(Coordinates spawn) {
        this.spawn = spawn;
        return this;
    }

    public EntityBuilder executor(ExecutorFactory<? extends Entity> executor) {
        this.executor = (ExecutorFactory<TridentEntity>) executor;
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

    public EntityBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public EntityBuilder silent(boolean silent) {
        this.silent = silent;
        return this;
    }

    public <T extends TridentEntity> T build(Class<T> entityType) {
        T entity = null;
        try {
            Constructor<T> constructor = entityType.getConstructor(UUID.class, Coordinates.class);
            entity = constructor.newInstance(uuid, spawn);
            entity.executor = executor != null ? executor.assign(entity) : Factories.threads().entityThread(entity);
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

        return entity;
    }

    public <T extends TridentEntity> T build(Class<T> entityType, ParameterValue<?>... parameterValues) {
        int paramLen = parameterValues.length;
        Class[] params = new Class[paramLen];
        Object[] args = new Object[paramLen];
        for (int i = 0; i < paramLen; i++) {
            ParameterValue<?> value = parameterValues[i];
            params[i] = value.clazz();
            args[i] = value.value();
        }

        T entity = null;
        try {
            Constructor<T> constructor = entityType.getConstructor(params);
            entity = constructor.newInstance(args);
            entity.executor = executor != null ? executor.assign(entity) : Factories.threads().entityThread(entity);
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

        return entity;
    }
}
