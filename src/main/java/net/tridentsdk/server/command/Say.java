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
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.ui.chat.ChatComponent;

import javax.annotation.concurrent.Immutable;

@Immutable
public class Say implements CmdListener {
    @Cmd(name = "say", help = "/say <message>", desc = "Broadcasts a message to all players")
    @Constrain(value = MinArgsConstraint.class, type = ConstraintType.INT, integer = 1)
    @Constrain(value = PermsConstraint.class, type = ConstraintType.STRING, str = "minecraft.say")
    public void say(String label, CmdSource source, String[] args) {
        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(arg).append(' ');
        }

        if (source.getCmdType() == CmdSourceType.PLAYER) {
            String msg = '[' + ((TridentPlayer) source).getName() + "] " + builder;
            for (TridentPlayer player : TridentPlayer.getPlayers().values()) {
                player.sendMessage(ChatComponent.create().setText(msg));
            }
            TridentServer.getInstance().getLogger().log(msg);
        } else {
            String msg = "[Server] " + builder;
            for (TridentPlayer player : TridentPlayer.getPlayers().values()) {
                player.sendMessage(ChatComponent.create().setText(msg));
            }
            TridentServer.getInstance().getLogger().log(msg);
        }
    }
}