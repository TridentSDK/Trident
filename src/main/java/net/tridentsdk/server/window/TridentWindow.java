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
package net.tridentsdk.server.window;

import net.tridentsdk.inventory.ItemStack;
import net.tridentsdk.window.Window;
import net.tridentsdk.server.player.TridentPlayer;

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
