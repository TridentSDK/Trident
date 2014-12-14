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
package net.tridentsdk.server.entity.decorate;

import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.decorate.DecorationAdapter;
import net.tridentsdk.entity.decorate.InventoryHolder;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.window.TridentWindow;
import net.tridentsdk.window.Window;
import net.tridentsdk.window.inventory.Inventory;
import net.tridentsdk.window.inventory.InventoryType;
import net.tridentsdk.window.inventory.Item;

public class DecoratedInventoryHolder extends DecorationAdapter<Entity> implements InventoryHolder {
    private final Window inventory;

    protected DecoratedInventoryHolder(Entity entity, final String string, final int size, InventoryType type) {
        super(entity);
        inventory = new TridentWindow(string, size, type);
    }

    @Override
    public Inventory getInventory() {
        return (Inventory) inventory;
    }

    @Override
    public Item getContent(int slot) {
        return inventory.getItems()[slot];
    }

    public void applyOpenWindow(Player player) {
        ((TridentWindow) inventory).sendTo((TridentPlayer) player);
    }
}
