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
package net.tridentsdk.api.trade;

import net.tridentsdk.api.inventory.ItemStack;

/**
 * Represents a pair of ItemStacks designated in a trade
 *
 * @author TridentSDK Team
 */
public class ItemPair {
    private final ItemStack one;
    private final ItemStack two;

    /**
     * Construct a ItemPair with only one input ItemStack The second ItemStack will be considered null and thus will be
     * treated as a single input when trading
     */
    public ItemPair(ItemStack one) {
        this(one, null);
    }

    /**
     * Construct a ItemPair with two input ItemStacks When used in a trade, the pair will be treated as a double input
     * when trading
     */
    public ItemPair(ItemStack one, ItemStack two) {
        this.one = one;
        this.two = two;
    }

    public ItemStack getFirstInput() {
        return this.one;
    }

    public ItemStack getSecondInput() {
        return this.two;
    }
}
