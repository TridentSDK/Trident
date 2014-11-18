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
package net.tridentsdk.api.plugin.cmd;

import net.tridentsdk.api.CommandIssuer;
import net.tridentsdk.api.ConsoleSender;
import net.tridentsdk.api.entity.living.Player;

public abstract class Command {
    /**
     * Called when this cmd is invoked by a player
     *
     * @param player
     * @param arguments may be null
     * @param alias
     */
    public void handlePlayer(Player player, String arguments, String alias) {
        // Method intentionally left blank
    }

    /**
     * Called when this cmd is invoked by the console
     *
     * @param sender
     * @param arguments may be null
     * @param alias
     */
    public void handleConsole(ConsoleSender sender, String arguments, String alias) {
        // Method intentionally left blank
    }

    /**
     * Called when this cmd is invoked by a player, console, or other sender
     *
     * @param sender
     * @param arguments may be null
     * @param alias
     */
    public void handle(CommandIssuer sender, String arguments, String alias) {
        // Method intentionally left blank
    }

    /**
     * Called if this cmd is overriden by another
     */
    public void notifyOverriden() {
        // Method intentionally left blank

    }

}
