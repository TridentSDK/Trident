/*
 *     TridentSDK - A Minecraft Server API
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.api.plugin.cmd;


import net.tridentsdk.api.CommandIssuer;
import net.tridentsdk.api.ConsoleSender;
import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.api.plugin.PluginLoadException;
import net.tridentsdk.api.plugin.annotation.CommandDescription;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {

    // TODO: Make this a dictionary tree for fast lookup
    private final HashMap<String, CommandData> commands = new HashMap<>();

    /**
     * Handles the message sent from the player, without the preceding "/"
     *
     * @param message
     */
    public void handleCommand(String message, CommandIssuer issuer) {
        if (message.isEmpty()) {
            return;
        }

        String[] contents = message.split(" ");

        String label = contents[0].toLowerCase();

        if (this.commands.containsKey(label)) {
            CommandData command = this.commands.get(label);
            String args = message.substring(label.length());

            if (issuer instanceof Player) {
                command.getCommand().handlePlayer(
                        (Player) issuer, args, contents[0]);
            } else if (issuer instanceof ConsoleSender) {
                command.getCommand().handleConsole(
                        (ConsoleSender) issuer, args, contents[0]);
            }
            command.getCommand().handle(issuer, args, contents[0]);
        }

        for (Map.Entry<String, CommandData> entry : this.commands.entrySet()) {
            if (entry.getValue().hasAlias(label)) {
                CommandData command = entry.getValue();
                String args = message.substring(label.length());
                if (issuer instanceof Player) {
                    command.getCommand().handlePlayer(
                            (Player) issuer, args, contents[0]);
                } else if (issuer instanceof ConsoleSender) {
                    command.getCommand().handleConsole(
                            (ConsoleSender) issuer, args, contents[0]);
                }
                command.getCommand().handle(issuer, args, contents[0]);
            }
        }
    }

    public int addCommand(Command command) throws PluginLoadException {

        CommandDescription description = command.getClass().getAnnotation(CommandDescription.class);

        if (description == null) {
            throw new PluginLoadException("Error in registering commands: Class does not have annotation " +
                    "\"CommandDescription\"!");
        }

        String name = description.name();
        int priority = description.priority();
        String[] aliases = description.aliases();
        String permission = description.permission();

        if (name == null || "".equals(name)) {
            throw new PluginLoadException("cmd does not declare a valid name!");
        }

        if (this.commands.containsKey(name.toLowerCase())) {
            if (this.commands.get(name.toLowerCase()).getPriority() > priority) {
                // put the new, more important cmd in place and notify the old cmd that it has been overriden
                this.commands.put(name.toLowerCase(), new CommandData(name, priority, aliases, permission, command))
                        .getCommand().notifyOverriden();
            } else {
                // don't register this cmd and notify it has been overriden
                command.notifyOverriden();
            }
        }
        // TODO: return something meaningful
        return 0;
    }

    private class CommandData {
        private final String permission;
        private final int priority;
        private final String[] aliases;
        private final String name;
        private final Command encapsulated;

        public CommandData(String name, int priority, String[] aliases, String permission, Command command) {
            this.priority = priority;
            this.name = name;
            this.aliases = aliases;
            this.permission = permission;
            this.encapsulated = command;
        }

        public Command getCommand() {
            return this.encapsulated;
        }

        public boolean hasAlias(String alias) {
            for (String string : this.aliases) {
                if (alias.equalsIgnoreCase(string)) {
                    return true;
                }
            }
            return false;
        }

        public int getPriority() {
            return this.priority;
        }
    }
}
