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

package net.tridentsdk.server.plugin;

import com.google.common.collect.Collections2;
import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.tridentsdk.ServerConsole;
import net.tridentsdk.Trident;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.meta.MessageBuilder;
import net.tridentsdk.plugin.Plugin;
import net.tridentsdk.plugin.PluginLoadException;
import net.tridentsdk.plugin.annotation.CommandDesc;
import net.tridentsdk.plugin.cmd.Command;
import net.tridentsdk.plugin.cmd.CommandIssuer;
import net.tridentsdk.plugin.cmd.Commands;
import net.tridentsdk.server.concurrent.TickSync;
import net.tridentsdk.util.TridentLogger;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Handles commands passed from the server
 *
 * @author The TridentSDK Team
 * @since 0.4-alpha
 */
public class CommandHandler extends ForwardingCollection<Command> implements Commands {
    // TODO: Make this a dictionary tree for fast lookup
    private final Map<String, CommandData> commands = new ConcurrentHashMap<>();

    public CommandHandler() {
        if (!Trident.isTrident())
            throw new RuntimeException(new IllegalAccessException("Only TridentSDK is allowed to make a new command handler"));
    }

    @Override
    protected Collection<Command> delegate() {
        return ImmutableSet.copyOf(Collections2.transform(commands.values(), CommandData::command));
    }

    @Override
    public void handle(final String message, final CommandIssuer issuer) {
        if (message.isEmpty()) {
            return;
        }

        final String[] contents = message.split(" ");
        final String label = contents[0].toLowerCase();
        final String args = message.substring(label.length() + (message.contains(" ") ? 1 : 0));
        final Set<CommandData> cmdData = findCommand(label);

        if (!cmdData.isEmpty()) {
            TickSync.sync(() -> {
                for (CommandData data : cmdData) {
                    handle(data.command(), issuer, args, contents, data);
                }
            });
        } else {
            // Command not found
            issuer.sendRaw("Command not found");
        }
    }

    private Set<CommandData> findCommand(String label) {
        Set<CommandData> dataSet = Sets.newHashSet();
        CommandData data = commands.get(label);

        if (data != null) {
            dataSet.add(data);
        }

        dataSet.addAll(commands.values().stream().filter(d -> d.hasAlias(label)).collect(Collectors.toList()));

        return dataSet;
    }

    private void handle(Command cmd, CommandIssuer issuer, String args, String[] contents, CommandData data) {
        if (!issuer.holdsPermission(data.permission)) {
            issuer.sendRaw(new MessageBuilder("You do not have permission").build().asJson());
            return;
        }

        if (issuer instanceof Player)
            cmd.handlePlayer((Player) issuer, args, contents[0]);
        else if (issuer instanceof ServerConsole)
            cmd.handleConsole((ServerConsole) issuer, args, contents[0]);

        cmd.handle(issuer, args, contents[0]);
    }

    @Override
    public int register(Plugin plugin, Command command) {
        CommandDesc description = command.getClass().getAnnotation(CommandDesc.class);

        if (description == null) {
            TridentLogger.error(new PluginLoadException(
                    "Error in registering commands: Class does not have annotation " + "\"CommandDesc\"!"));
            return 0;
        }

        String name = description.name();
        int priority = description.priority();
        String[] aliases = description.aliases();
        String permission = description.permission();

        if (name == null || "".equals(name)) {
            TridentLogger.error(new PluginLoadException("cmd does not declare a valid name!"));
            return 0;
        }

        String lowerCase = name.toLowerCase();
        CommandData data = commands.get(lowerCase);
        CommandData newData = new CommandData(name, priority, aliases, permission, command, plugin);

        if (data != null) {
            if (commands.get(lowerCase).priority() > priority) {
                // put the new, more important cmd in place and notify the old cmd that it has been overridden
                commands.put(lowerCase, newData).command().notifyOverriden();
            } else {
                // don't register this cmd and notify it has been overridden
                command.notifyOverriden();
            }
        } else {
            commands.put(name, newData);
        }

        // TODO: return something meaningful
        return 0;
    }

    @Override
    public void unregister(Class<? extends Command> cls) {
        commands.entrySet().stream().filter(e -> e.getValue().command().getClass().equals(cls)).forEach(e -> commands.remove(e.getKey()));
    }

    private static class CommandData {
        private final String permission;
        private final int priority;
        private final String[] aliases;
        private final String name;
        private final Command encapsulated;
        private final Plugin plugin;

        public CommandData(String name, int priority, String[] aliases, String permission, Command command,
                           Plugin plugin) {
            this.priority = priority;
            this.name = name;
            this.aliases = aliases;
            this.permission = permission;
            this.encapsulated = command;
            this.plugin = plugin;
        }

        public Command command() {
            return this.encapsulated;
        }

        public boolean hasAlias(String alias) {
            if (name.equals(alias)) return true;

            for (String string : this.aliases) {
                if (alias.equalsIgnoreCase(string)) {
                    return true;
                }
            }
            return false;
        }

        public int priority() {
            return this.priority;
        }

        public Plugin plugin() {
            return plugin;
        }
    }
}