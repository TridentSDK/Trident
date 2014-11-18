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
package net.tridentsdk.api.entity;

import net.tridentsdk.api.inventory.Inventory;
import net.tridentsdk.api.inventory.ItemStack;

/**
 * Represents an Entity that holds an Inventory
 *
 * @author TridentSDK Team
 */
public interface InventoryHolder extends Entity {
    /*
     * TODO: Convert the return types into a valid representation of their respective objects
     */

    /**
     * The Inventory that this entity holds
     *
     * @return the Inventory that this entity holds
     */
    Inventory getInventory();

    /**
     * The contents this slot
     *
     * @param slot the target slot
     * @return the contents of the specified slot
     */
    ItemStack getContent(int slot);
}
