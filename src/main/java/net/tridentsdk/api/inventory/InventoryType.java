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
package net.tridentsdk.api.inventory;

/**
 * Type of inventories
 *
 * @author The TridentSDK Team
 */
public enum InventoryType {
    CHEST("minecraft:chest"),
    CRAFTING_TABLE("minecraft:crafting_table"),
    FURNACE("minecraft:furnace"),
    DISPENSER("minecraft:despenser"),
    ENCHANTING_TABLE("minecraft:enchanting_table"),
    BREWING_STAND("minecraft:brewing_stand"),
    VILLAGER_TRADE("minecraft:villager"),
    BEACON("minecraft:beacon"),
    ANVIL("minecraft:anvil"),
    HOPPER("minecraft:hopper"),
    DROPPER("minecraft:dropper"),
    HORSE("minecraft:horse");

    private final String s;

    InventoryType(String s) {
        this.s = s;
    }

    /**
     * Returns the Minecraft ID for the entity or block which holds the InventoryType
     *
     * @return String Minecraft ID for the entity or block which holds the InventoryType
     */
    @Override
    public String toString() {
        return this.s;
    }
}
