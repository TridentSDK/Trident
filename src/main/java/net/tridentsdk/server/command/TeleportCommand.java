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

import net.tridentsdk.base.Position;
import net.tridentsdk.command.*;
import net.tridentsdk.command.params.ParamsAnnotations;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.ui.chat.ChatColor;
import net.tridentsdk.ui.chat.ChatComponent;

public class TeleportCommand implements CommandListener {

    @Command(name = "teleport", aliases = "tp", help = "/teleport <player> <x> <y> <z> [<pitch> <yaw>]", desc = "Teleports the given player to the given XYZ")
    @ParamsAnnotations.PermissionRequired("minecraft.tp")
    @ParamsAnnotations.AllowedSourceTypes(CommandSourceType.PLAYER)
    public void teleport(CommandSource source, String[] args, @ParamsAnnotations.PlayerExactMatch Player player, double x, double y, double z, @ParamsAnnotations.MaxCount(2) float... direction) {
        if (player == null) {
            source.sendMessage(ChatComponent.create().setColor(ChatColor.RED).setText("No player by that name is online"));
        } else {
            float pitch = direction.length > 0 ? direction[0] : 0;
            float yaw = direction.length > 1 ? direction[1] : 0;
            player.setPosition(new Position(player.getWorld(), x, y, z, pitch, yaw));
        }
    }

}
