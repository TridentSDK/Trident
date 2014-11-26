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
package net.tridentsdk.window;

import net.tridentsdk.api.inventory.ItemStack;
import net.tridentsdk.api.window.Window;
import net.tridentsdk.player.TridentPlayer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * An inventory window, wherever and whatever is holding it or having it open
 *
 * @author The TridentSDK Team
 */
public abstract class TridentWindow implements Window {
    /**
     * Counter for window ids, initial value is 2 to avoid confusion with a window and a player inventory
     */
    private static final AtomicInteger counter = new AtomicInteger(2);

    private final int id;
    private final String name;
    private final int length;
    private final ItemStack[] contents;

    /**
     * Builds a new inventory window
     *
     * @param name   the title of the inventory
     * @param length the amount of slots in the inventory (should be multiple of 9)
     */
    public TridentWindow(String name, int length) {
        this.name = name;
        this.length = length;
        this.id = counter.addAndGet(1);
        this.contents = new ItemStack[length];
    }

    /**
     * Builds a new inventory window
     *
     * @param length the amount of slots in the inventory (should be multiple of 9)
     */
    public TridentWindow(int length) {
        this("", length);
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public ItemStack[] getContents() {
        return this.contents;
    }

    @Override
    public int getLength() {
        return this.length;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setSlot(int index, ItemStack value) {
        this.contents[index] = value;
        // TODO: update client
    }

    public abstract void sendTo(TridentPlayer player);
}
