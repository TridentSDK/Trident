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

package net.tridentsdk.server.world;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import net.tridentsdk.Coordinates;
import net.tridentsdk.base.Substance;
import net.tridentsdk.base.Tile;
import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.projectile.Projectile;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.util.Vector;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Set;

public class TridentTile implements Tile {
    private final Coordinates location;
    /**
     * Describes projectile logic
     */
    private final Set<WeakReference<Projectile>> projectiles = Sets.newSetFromMap(
            Factories.collect().<WeakReference<Projectile>, Boolean>createMap());
    /**
     * The type for this block
     */
    protected volatile Substance material;
    /**
     * The block metadata
     */
    protected byte data;

    /**
     * Constructs the wrapper representing the block
     *
     * @param location Location of the Block
     */
    @InternalUseOnly
    public TridentTile(Coordinates location) {
        this.location = location;

        // Note: Avoid recursion by not creating a new instance from World#tileAt(Location)
        Tile worldBlock = location.world().tileAt(location);
        this.material = worldBlock.substance();
    }

    public TridentTile(Coordinates location, Substance substance, byte meta) {
        this.location = location;
        this.material = substance;
        this.data = meta;
    }

    @Override
    public Substance substance() {
        return this.material;
    }

    @Override
    public void setSubstance(Substance material) {
        this.material = material;
    }

    @Override
    public Coordinates location() {
        return this.location;
    }

    @Override
    public byte meta() {
        return this.data;
    }

    @Override
    public void setMeta(byte data) {
        this.data = data;
    }

    @Override
    public Tile relativeTile(Vector vector) {
        return new TridentTile(this.location.relative(vector));
    }

    @Override
    public boolean isImpaledEntity() {
        return false;
    }

    @Override
    public boolean isImpaledTile() {
        return true;
    }

    @Override
    public Entity impaledEntity() {
        return null;
    }

    @Override
    public Tile impaledTile() {
        return this;
    }

    @Override
    public void put(Projectile projectile) {
        this.projectiles.add(new WeakReference<>(projectile));
    }

    @Override
    public boolean remove(Projectile projectile) {
        return this.projectiles.remove(new WeakReference<>(projectile));
    }

    @Override
    public void clear() {
        this.projectiles.clear();
    }

    @Override
    public Collection<Projectile> projectiles() {
        return new ImmutableSet.Builder<Projectile>().addAll(
                Iterators.transform(projectiles.iterator(), new Function<WeakReference<Projectile>, Projectile>() {
                                        @Nullable
                                        @Override
                                        public Projectile apply(WeakReference<Projectile> projectileWeakReference) {
                                            return projectileWeakReference.get();
                                        }
                                    })).build();
    }
}
