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
import net.tridentsdk.inventory.Inventory;
import net.tridentsdk.inventory.InventoryType;
import net.tridentsdk.inventory.Item;
import net.tridentsdk.ui.chat.ChatComponent;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Implementation of an arbitrary inventory.
 */
@ThreadSafe // TODO
public class TridentInventory implements Inventory {
    /**
     * Inventory type
     */
    @Getter
    private final InventoryType type;
    /**
     * The amount of slots available in this inventory
     */
    @Getter
    private final int size;

    /**
     * Constructs a new inventory with the given type and
     * slot amount.
     *
     * @param type the inventory type
     * @param size the amount of slots that the new
     * inventory should contain
     */
    public TridentInventory(InventoryType type, int size) {
        this.type = type;
        this.size = size;
    }

    @Override
    public boolean add(Item item, int quantity) {
        return false;
    }

    @Override
    public Item add(int slot, Item item, int quantity) {
        return null;
    }

    @Nullable
    @Override
    public Item remove(int slot, int quantity) {
        return null;
    }

    @Nullable
    @Override
    public Item get(int slot) {
        return null;
    }

    @Override
    public ChatComponent getTitle() {
        return null;
    }

    @Override
    public void setTitle(ChatComponent title) {
    }
}