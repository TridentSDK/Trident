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

import net.tridentsdk.command.Command;
import net.tridentsdk.command.CommandListener;
import net.tridentsdk.command.CommandSource;
import net.tridentsdk.command.CommandSourceType;
import net.tridentsdk.command.annotation.AllowedSourceTypes;
import net.tridentsdk.command.annotation.PermissionRequired;
import net.tridentsdk.command.annotation.PlayerExactMatch;
import net.tridentsdk.entity.living.EntityPlayer;
import net.tridentsdk.ui.chat.ChatColor;
import net.tridentsdk.ui.chat.ChatComponent;

public class OpCommand implements CommandListener {

    @Command(name = "op", help = "/op <player>", desc = "Sets the player to an operator")
    @PermissionRequired("minecraft.op")
    @AllowedSourceTypes({ CommandSourceType.PLAYER, CommandSourceType.CONSOLE })
    public void op(CommandSource source, String[] args, @PlayerExactMatch EntityPlayer player) {
        if (player == null) {
            source.sendMessage(ChatComponent.create().setColor(ChatColor.RED).setText("No player by the name '" + args[1] + "' is online!"));
        } else {
            player.setOp(true);
        }
    }
}
