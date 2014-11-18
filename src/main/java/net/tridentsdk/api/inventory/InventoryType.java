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
