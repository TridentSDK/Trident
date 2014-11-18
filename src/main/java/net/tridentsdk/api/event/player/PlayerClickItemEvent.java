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

import net.tridentsdk.api.event.Cancellable;
import net.tridentsdk.api.event.Event;
import net.tridentsdk.api.window.Window;

public class PlayerClickItemEvent extends Event implements Cancellable {

    private final Window window;
    private final short clickedSlot;
    private final int actionId;

    private boolean cancelled;

    public PlayerClickItemEvent(Window window, short clickedSlot, int actionId) {
        this.window = window;
        this.clickedSlot = clickedSlot;
        this.actionId = actionId;
        this.cancelled = false;
    }

    public Window getWindow() {
        return this.window;
    }

    public short getClickedSlot() {
        return this.clickedSlot;
    }

    public int getActionId() {
        return this.actionId;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
