package net.tridentsdk.server.entity.decorate;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import io.netty.util.internal.chmv8.ConcurrentHashMapV8;
import net.tridentsdk.base.Block;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.decorate.Impalable;
import net.tridentsdk.entity.projectile.Projectile;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Set;

public abstract class DecoratedImpalable implements Impalable {
    private Entity impaledEntity;
    private Block impaledTile;
    private final Set<WeakReference<Projectile>> projectiles = Sets.newSetFromMap(
            new ConcurrentHashMapV8<WeakReference<Projectile>, Boolean>());

    @Override
    public abstract boolean isImpaledEntity();

    @Override
    public boolean isImpaledTile() {
        return !isImpaledEntity();
    }

    @Override
    public Entity impaledEntity() {
        return impaledEntity;
    }

    @Override
    public Block impaledTile() {
        return impaledTile;
    }

    @Override
    public void put(Projectile projectile) {
        projectiles.add(new WeakReference<>(projectile));
    }

    @Override
    public boolean remove(Projectile projectile) {
        return projectiles.remove(new WeakReference<>(projectile));
    }

    @Override
    public void clear() {
        projectiles.clear();
    }

    @Override
    public Collection<Projectile> projectiles() {
        return new ImmutableSet.Builder<Projectile>().addAll(Iterators.transform(projectiles.iterator(),
                new Function<WeakReference<Projectile>, Projectile>() {
            @Nullable
            @Override
            public Projectile apply(WeakReference<Projectile> projectileWeakReference) {
                return projectileWeakReference.get();
            }
        })).build();
    }
}
