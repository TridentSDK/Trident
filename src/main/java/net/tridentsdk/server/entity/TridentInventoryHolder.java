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
import net.tridentsdk.docs.Volatile;
import net.tridentsdk.entity.decorate.InventoryHolder;
import net.tridentsdk.window.inventory.Inventory;

import java.util.UUID;

/**
 * An entity that is able to hold an inventory
 *
 * @author The TridentSDK Team
 */
public abstract class TridentInventoryHolder extends TridentLivingEntity implements InventoryHolder {
    private final Object BARRIER;
    /**
     * The inventory held by the entity
     */
    @Volatile(policy = "Do not set after construction", reason = "Barrier", fix = "Set in constructor, do not change")
    protected Inventory inventory;

    /**
     * Inherits constructor from {@link TridentLivingEntity}
     */
    public TridentInventoryHolder(UUID id, Position spawnLocation) {
        super(id, spawnLocation);
        BARRIER = new Object();
    }

    @Override
    public Inventory inventory() {
        return this.inventory;
    }
}
