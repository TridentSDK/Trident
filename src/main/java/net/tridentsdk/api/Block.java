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
package net.tridentsdk.api;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.entity.Impalable;
import net.tridentsdk.api.entity.Projectile;
import net.tridentsdk.api.util.Vector;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

/**
 * A basic structure in minecraft, a material bearing piece set at a given location
 *
 * @author The TridentSDK Team
 */
public class Block implements Impalable {
    private final Location location;
    protected Material material;
    protected byte data;
    /**
     * Describes projectile logic
     */
    public final List<WeakReference<Projectile>> hit = Collections.synchronizedList(
            Lists.<WeakReference<Projectile>>newArrayList());

    /**
     * Constructs the wrapper representing the block
     *
     * @param location Location of the Block
     */
    public Block(Location location) {
        this.location = location;

        // Note: Avoid recursion by not creating a new instance from World#getBlockAt(Location)
        Block worldBlock = location.getWorld().getBlockAt(location);

        this.material = worldBlock.material;
    }

    /**
     * For internal use only
     */
    protected Block(Location location, boolean createdByServer) {
        this.location = location;
    }

    /**
     * Returns the Material of the Block
     *
     * @return Material of the Block
     */
    public Material getType() {
        return this.material;
    }

    // TODO: Verify the redundancy
    public void setType(Material material) {
        this.material = material;
    }

    // TODO: Verify the redundancy
    public Material getMaterial() {
        return this.material;
    }

    /**
     * Set the Material of this Block
     *
     * @param material Material to set this Block to
     */
    public void setMaterial(Material material) {
        this.material = material;
    }

    /**
     * Returns the Location of the Block
     *
     * @return Location of the Block
     */
    public Location getLocation() {
        return this.location;
    }

    public byte getData() {
        return this.data;
    }

    public void setData(byte data) {
        this.data = data;
    }

    /**
     * Returns a block immediately to the direction specified
     *
     * @param vector the direction to look for the block adjacent to the current
     * @return the block adjacent to the current
     */
    public Block getRelative(Vector vector) {
        return new Block(this.location.getRelative(vector));
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
    public Block impaledTile() {
        if (!this.isImpaledTile())
            return null;
        return this;
    }

    @Override
    public void put(Projectile projectile) {
        this.hit.add(new WeakReference<>(projectile));
    }

    @Override
    public boolean remove(Projectile projectile) {
        return this.hit.remove(new WeakReference<>(projectile));
    }

    @Override
    public void clear() {
        // TODO remove the projectile entities
        this.hit.clear();
    }

    @Override
    public List<Projectile> projectiles() {
        return new ImmutableList.Builder<Projectile>().addAll(Lists.transform(this.hit, new Function<WeakReference<Projectile>,
                Projectile>() {
            @Override
            public Projectile apply(WeakReference<Projectile> projectileWeakReference) {
                return projectileWeakReference.get();
            }
        })).build();
    }
}
