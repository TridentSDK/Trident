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
package net.tridentsdk.trade;

import net.tridentsdk.api.inventory.ItemStack;
import net.tridentsdk.api.trade.ItemPair;

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
