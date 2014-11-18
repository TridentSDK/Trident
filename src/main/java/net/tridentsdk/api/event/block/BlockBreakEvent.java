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
package net.tridentsdk.api.event.block;

import net.tridentsdk.api.Block;
import net.tridentsdk.api.BlockFace;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.event.Cancellable;
import net.tridentsdk.api.inventory.ItemStack;

/**
 * Called whenever a Block is broken
 */
public class BlockBreakEvent extends BlockEvent implements Cancellable {

    private final Player player;
    private final BlockFace blockFace;
    private final ItemStack itemInHand;
    private boolean cancel;

    /**
     * @param player     Player associated with this event
     * @param block      Block associated with this event
     * @param blockFace  BlockFace
     * @param itemInHand ItemStack
     */
    public BlockBreakEvent(Player player, Block block, BlockFace blockFace, ItemStack itemInHand) {
        super(block);
        this.player = player;
        this.blockFace = blockFace;
        this.itemInHand = itemInHand;
    }

    /**
     * Return if the event is cancelled
     *
     * @return true if cancelled
     */
    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    /**
     * Set if the event is cancelled
     *
     * @param cancel Boolean cancellation state of event
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * Returns the item in the player's hand
     *
     * @return ItemStack in the player's hand
     */
    public ItemStack getItemInHand() {
        return this.itemInHand;
    }

    /**
     * Returns the block face clicked to break this block
     *
     * @return BlockFlace of the clicked block
     */
    public BlockFace getBlockFace() {
        return this.blockFace;
    }

    /**
     * Get the player associated with this event
     *
     * @return Player assoctaed with this event
     */
    public Player getPlayer() {
        return this.player;
    }
}
