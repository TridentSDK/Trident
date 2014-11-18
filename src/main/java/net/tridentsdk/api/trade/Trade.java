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
 * Represents a Trade offered by an {@link net.tridentsdk.api.entity.Tradeable}
 *
 * @author TridentSDK Team
 */
public interface Trade {
    /**
     * Whether or not this trade rewards xp
     *
     * @return Whether or not this trade should reward xp
     */
    boolean rewardExp();

    /**
     * How many times this trade can be fulfilled
     *
     * @return the number of times this trade can be fulfilled
     */
    int maxUses();

    /**
     * How many times this trade has been fulfilled
     *
     * @return how many times this trade has been fulfilled
     */
    int uses();

    /**
     * The itemstack given as a result of this trade
     *
     * @return the itemstack that is given as a result of fulfilling this trade
     */
    ItemStack offer();

    /**
     * The itemstacks required to be input in order to fulfilled this trade This accepts both implementations of
     * ItemPair, including a null second offer
     *
     * @return the input that will fulfill this trade
     */
    ItemPair input();
}
