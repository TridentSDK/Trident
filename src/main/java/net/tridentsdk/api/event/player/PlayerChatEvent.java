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

import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.event.Cancellable;

public class PlayerChatEvent extends PlayerEvent implements Cancellable {

    private boolean cancel;

    private String message;

    /**
     * @param player  the player associated with this event
     * @param message the message sent by the player
     */

    public PlayerChatEvent(Player player, String message) {
        super(player);
        this.setMessage(message);
    }

    /**
     * @return return the message sent
     */

    public String getMessage() {
        return this.message;
    }

    /**
     * @param message the message associated with this event
     */

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
