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
package net.tridentsdk.api.event.player;

import net.tridentsdk.api.Block;
import net.tridentsdk.api.BlockFace;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.event.block.BlockPlaceEvent;
import net.tridentsdk.api.inventory.ItemStack;

/**
 * Called when a player empties a bucket
 */
public class PlayerBucketEmptyEvent extends BlockPlaceEvent {

    private final ItemStack blockInHand;

    public PlayerBucketEmptyEvent(Player player, Block block, Block blockClicked,
                                  BlockFace faceClicked, ItemStack blockInHand) {
        super(player, block, blockClicked, faceClicked);
        this.blockInHand = blockInHand;
    }

    public ItemStack getBlockInHand() {
        return this.blockInHand;
    }
}
