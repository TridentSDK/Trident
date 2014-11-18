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
package net.tridentsdk.api.event.player;

import net.tridentsdk.api.Block;
import net.tridentsdk.api.BlockFace;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.event.block.BlockBreakEvent;
import net.tridentsdk.api.inventory.ItemStack;

/**
 * Called when a player fills a bucket
 */
public class PlayerBucketFillEvent extends BlockBreakEvent {

    public PlayerBucketFillEvent(Player player, Block block, BlockFace blockFace, ItemStack itemInHand) {
        super(player, block, blockFace, itemInHand);
    }
}
