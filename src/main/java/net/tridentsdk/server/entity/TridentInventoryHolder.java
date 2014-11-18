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
package net.tridentsdk.server.entity;

import net.tridentsdk.api.Location;
import net.tridentsdk.api.entity.InventoryHolder;
import net.tridentsdk.api.inventory.Inventory;
import net.tridentsdk.api.inventory.ItemStack;

import java.util.UUID;

/**
 * An entity that is able to hold an inventory
 *
 * @author The TridentSDK Team
 */
public abstract class TridentInventoryHolder extends TridentLivingEntity implements InventoryHolder {
    /**
     * The inventory held by the entity
     */
    protected Inventory inventory;

    /**
     * Inherits constructor from {@link net.tridentsdk.server.entity.TridentLivingEntity}
     */
    public TridentInventoryHolder(UUID id, Location spawnLocation) {
        super(id, spawnLocation);
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public ItemStack getContent(int slot) {
        return this.inventory.getContents()[slot];
    }
}
