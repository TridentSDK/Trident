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
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.ui.chat.ChatColor;
import net.tridentsdk.ui.chat.ChatComponent;

import javax.annotation.concurrent.Immutable;
import java.util.Map;

@Immutable
public class Kick implements CmdListener {
    @Cmd(name = "kick", help = "/kick <player> [reason]", desc = "Kicks a player from the server")
    @Constrain(value = MinArgsConstraint.class, type = ConstraintType.INT, integer = 1)
    public void kick(String label, CmdSource source, String[] args) {
        String player = args[0];
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            builder.append(args[i]).append(' ');
        }
        String reason = builder.toString();
        reason = reason.isEmpty() ? "Kicked by an operator." : reason;

        Player p = Player.byName(player);

        Map<String, Player> map = Player.search(player);
        if (p == null) {
            if (map.size() == 1) {
                p = map.values().stream().findFirst().orElseThrow(RuntimeException::new);
            } else {
                source.sendMessage(ChatComponent.create().setColor(ChatColor.RED).
                        setText("No player by the name \"" + player + "\" is online"));
                return;
            }
        }

        p.kick(ChatComponent.text(reason));
        TridentServer.getInstance().getLogger().log("Kicked player " + player + " for: " + reason);
    }
}