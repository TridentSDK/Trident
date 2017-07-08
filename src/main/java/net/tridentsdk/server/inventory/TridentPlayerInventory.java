/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
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

import lombok.Getter;
import lombok.Setter;
import net.tridentsdk.inventory.InventoryType;
import net.tridentsdk.inventory.Item;
import net.tridentsdk.inventory.PlayerInventory;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.PacketOut;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Implements an inventory that is held by the player
 */
@ThreadSafe
public class TridentPlayerInventory extends TridentInventory implements PlayerInventory {
    /**
     * The currently selected hotbar slot
     */
    @Getter
    @Setter
    private volatile int selectedSlot;
    /**
     * The player receiving the inventory
     */
    private final NetClient client;

    public TridentPlayerInventory(NetClient client) {
        super(InventoryType.PLAYER, 46);
        this.client = client;
    }

    @Nonnull
    @Override
    public Item getHeldItem() {
        return this.get(36 + this.selectedSlot);
    }

    @Nonnull
    @Override
    public Item getOffHeldItem() {
        return this.get(45);
    }

    @Override
    protected void sendViewers(PacketOut packetOut) {
        this.client.sendPacket(packetOut);
    }
}