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
package net.tridentsdk.server.command;


import net.tridentsdk.entity.living.Player;
import net.tridentsdk.meta.ChatColor;
import net.tridentsdk.plugin.annotation.CommandDescription;
import net.tridentsdk.plugin.cmd.Command;

@CommandDescription(name = "teleport", permission = "trident.teleport", aliases = "tp")
public class TeleportCommand extends Command {
    @Override
    public void handlePlayer(Player player, String arguments, String alias) {
        String [] args = arguments.split(" ");
        if (args.length < 3) {
           /* player.sendMessage(ServerCommandRegistrar.SERVER_PREFIX + ServerCommandRegistrar.ERROR_PREFIX
                    + "Not enough arguments, check command. Usage: /tp <name> (x) (y) (z)");*/
            return;
        }
        
        try {
            double x = Double.parseDouble(args[0]);
            double y = Double.parseDouble(args[1]);
            double z = Double.parseDouble(args[2]);
            player.teleport(x, y, z);
            //player.sendMessage(ServerCommandRegistrar.SERVER_PREFIX + "Teleporting...");
        } catch (NumberFormatException ex) {
            /*player.sendMessage(ServerCommandRegistrar.SERVER_PREFIX + ServerCommandRegistrar.ERROR_PREFIX
                    + "Feature not implemented yet.");*/
        }
    }
}
