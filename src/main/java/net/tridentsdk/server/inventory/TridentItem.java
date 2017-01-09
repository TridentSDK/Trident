/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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
package net.tridentsdk.server.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.tridentsdk.base.Substance;
import net.tridentsdk.inventory.Item;
import net.tridentsdk.meta.ItemMeta;

/**
 * Implementation of an inventory item.
 */
@Getter
@AllArgsConstructor
public class TridentItem implements Item {
    /**
     * The item's substance type.
     */
    private final Substance substance;
    /**
     * The amount of items in this stack.
     */
    private int count;
    /**
     * The 4-bit item data.
     */
    private byte damage;
    /**
     * The item metadata
     */
    private ItemMeta meta;
}