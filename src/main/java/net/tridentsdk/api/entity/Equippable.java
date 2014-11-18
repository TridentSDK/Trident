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

import net.tridentsdk.api.inventory.ItemStack;

/**
 * Represents an entity that can be equipped
 *
 * @author TridentSDK Team
 */
public interface Equippable extends Entity {
    /**
     * This entity's equipment
     * <p/>
     * <p>Layout:
     * <ul>
     * <li>Index 0: Helmet</li>
     * <li>Index 1: Chestplate</li>
     * <li>Index 2: Leggings</li>
     * <li>Index 3: Boots</li>
     * </ul></p>
     *
     * @return this entity's equipment
     */
    ItemStack[] getEquipment();
}
