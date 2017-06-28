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
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.ui.chat.ChatColor;
import net.tridentsdk.ui.chat.ChatComponent;

import javax.annotation.concurrent.Immutable;
import java.util.Map;

@Immutable
public class Tp implements CmdListener {
    @Cmd(name = "tp", help = "/teleport <player> <x> <y> <z> [<pitch> <yaw>]", desc = "Teleports the given player to the given XYZ")
    @Constrain(value = SourceConstraint.class, type = ConstraintType.SOURCE, src = CmdSourceType.PLAYER)
    @Constrain(value = MinArgsConstraint.class, type = ConstraintType.INT, integer = 4)
    @Constrain(value = MaxArgsConstraint.class, type = ConstraintType.INT, integer = 6)
    @Constrain(value = PermsConstraint.class, type = ConstraintType.STRING, str = "minecraft.tp")
    public void teleport(String label, CmdSource source, String[] args) {
        String player = args[0];
        Player p = Player.byName(player);

        if (p == null) {
            Map<String, Player> map = Player.search(player);
            if (map.size() == 1) {
                p = map.values().stream().findFirst().orElseThrow(RuntimeException::new);
            } else {
                source.sendMessage(ChatComponent.create().setColor(ChatColor.RED).
                        setText("No player by the name \"" + player + "\" is online"));
                return;
            }
        }

        Position position = p.getPosition();

        if (args.length == 4) {
            try {
                int x = Integer.parseInt(args[1]);
                int y = Integer.parseInt(args[2]);
                int z = Integer.parseInt(args[3]);
                position.set(x, y, z);

                // TODO teleport
                p.setPosition(position);
            } catch (NumberFormatException e) {
                source.sendMessage(ChatComponent.create().setColor(ChatColor.RED).
                        setText("Given coordinates were not numbers"));
            }
        } else if (args.length == 6) {
            try {
                int x = Integer.parseInt(args[1]);
                int y = Integer.parseInt(args[2]);
                int z = Integer.parseInt(args[3]);
                float pitch = Float.parseFloat(args[4]);
                float yaw = Float.parseFloat(args[5]);
                position.set(x, y, z);
                position.setPitch(pitch);
                position.setYaw(yaw);

                p.setPosition(position);
            } catch (NumberFormatException e) {
                source.sendMessage(ChatComponent.create().setColor(ChatColor.RED).
                        setText("Given coordinates were not numbers"));
            }
        }
    }
}