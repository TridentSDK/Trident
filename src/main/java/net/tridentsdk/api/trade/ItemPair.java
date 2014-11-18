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
