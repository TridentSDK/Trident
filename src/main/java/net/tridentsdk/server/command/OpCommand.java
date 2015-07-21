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

import net.tridentsdk.ServerConsole;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.meta.ChatColor;
import net.tridentsdk.meta.MessageBuilder;
import net.tridentsdk.plugin.annotation.CommandDescription;
import net.tridentsdk.plugin.cmd.Command;
import net.tridentsdk.registry.Registered;
import net.tridentsdk.service.PermissionHolder;

import java.util.UUID;

@CommandDescription(name = "op", permission = "trident.op")
public class OpCommand extends Command {
    @Override
    public void handlePlayer(Player player, String arguments, String alias) {
        String[] args = arguments.split(" ");
        if (args.length != 1) {
            new MessageBuilder("Those are the wrong arguments").color(ChatColor.RED).sendTo(player);
            return;
        }

        UUID uuid = null;
        for (Player p : Registered.players()) {
            if (p.name().equals(args[0])) {
                uuid = p.uniqueId();
            }
        }

        if (uuid == null) {
            new MessageBuilder("There is no player by the name " + args[0]).color(ChatColor.RED).sendTo(player);
            return;
        }

        Registered.statuses().op(uuid);
        new MessageBuilder("[CONSOLE: Opped " + uuid + "]").color(ChatColor.GRAY).sendTo(player);
    }

    @Override
    public void handleConsole(ServerConsole sender, String arguments, String alias) {
        String[] args = arguments.split(" ");
        if (args.length != 1) {
            sender.sendRaw(ChatColor.RED + "Those are the wrong arguments");
            return;
        }

        UUID uuid = null;
        for (Player p : Registered.players()) {
            if (p.name().equals(args[0])) {
                uuid = p.uniqueId();
            }
        }

        if (uuid == null) {
            sender.sendRaw(ChatColor.RED + "There is no player by the name " + args[0]);
            return;
        }

        Registered.statuses().op(uuid);
        sender.sendRaw(ChatColor.GREEN + "Opped " + uuid);

        final UUID finalUuid = uuid;
        Registered.players().stream().filter(PermissionHolder::opped).forEach(player ->
                new MessageBuilder("[CONSOLE: Opped " + finalUuid + "]").color(ChatColor.GRAY).sendTo(player));
    }
}
