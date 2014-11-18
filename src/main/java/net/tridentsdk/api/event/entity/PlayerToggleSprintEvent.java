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
package net.tridentsdk.api.event.entity;

import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.event.Cancellable;
import net.tridentsdk.api.event.player.PlayerEvent;

/**
 * Called when a player tries to start sprinting, even if hunger would otherwise prevent them from sprinting
 */
public class PlayerToggleSprintEvent extends PlayerEvent implements Cancellable {
    private final boolean toggle;
    private boolean cancelled;

    public PlayerToggleSprintEvent(Player player, boolean toggle) {
        super(player);
        this.toggle = toggle;
    }

    /**
     * @return true if the player is trying to sprint
     */
    public boolean sprintOn() {
        return this.toggle;
    }

    /**
     * @return true if the player is trying to stop sprinting
     */
    public boolean sprintOff() {
        return !this.toggle;
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
