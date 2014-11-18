/*
 *     TridentSDK - A Minecraft Server API
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
package net.tridentsdk.inventory;

import net.tridentsdk.api.Material;

/**
 * Inventory item, holding all properties of the item
 *
 * @author The TridentSDK Team
 */
public class ItemStack {
    private final int id;
    private final Material mat;

    private volatile short quantity;
    private volatile short damageValue;

    public ItemStack(Material mat) {
        this(mat, (short) 1);
    }

    public ItemStack(Material mat, short quantity) {
        this.id = Integer.valueOf(mat.getId());
        this.mat = mat;

        this.quantity = quantity;
        this.damageValue = (short) 100; // psudeo-value
    }

    public int getId() {
        return this.id;
    }

    public Material getType() {
        return this.mat;
    }

    public short getQuantity() {
        return this.quantity;
    }

    public short getDamageValue() {
        return this.damageValue;
    }

    public void setQuantity(short quantity) {
        this.quantity = quantity;
    }

    public void setDamageValue(short damageValue) {
        this.damageValue = damageValue;
    }
}
