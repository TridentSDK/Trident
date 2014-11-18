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
