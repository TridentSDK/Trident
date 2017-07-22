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
package net.tridentsdk.server.command;

import net.tridentsdk.command.*;
import net.tridentsdk.command.params.ParamsAnnotations;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.ui.chat.ChatComponent;

import javax.annotation.concurrent.Immutable;

@Immutable
public class KickCommand implements CommandListener {

    @Command(name = "kick", help = "/kick <player> [reason]", desc = "Kicks a player from the server")
    @ParamsAnnotations.PermissionRequired("minecraft.kick")
    public void kick(CommandSource source, String[] args, @ParamsAnnotations.PlayerExactMatch Player player, String... reason) {
        if (player != null) {
            String reasonString = reason.length == 0 ? "Kicked by an operator." : String.join(" ", reason);
            player.kick(ChatComponent.text(reasonString));
            TridentServer.getInstance().getLogger().log("Kicked player " + player.getName() + " for: " + reason);
        } else {
            source.sendMessage(ChatComponent.text("No player by the name '" + args[1] + "' is online."));
        }
    }
}
