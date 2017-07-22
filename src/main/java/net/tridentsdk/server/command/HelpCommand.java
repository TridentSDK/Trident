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

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import net.tridentsdk.command.*;
import net.tridentsdk.command.constraint.ConstraintType;
import net.tridentsdk.command.constraint.ConstraintsAnnotations;
import net.tridentsdk.command.constraint.MaxArgsConstraint;
import net.tridentsdk.command.constraint.PermsConstraint;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.ui.chat.ChatColor;
import net.tridentsdk.ui.chat.ChatComponent;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Immutable
public class HelpCommand implements CommandListener {
    private static final int PAGE_SIZE = 5;

    @Command(name = "help", aliases = "?", help = "/help [command] [page]", desc = "Displays a help message, or looks for one if a command is provided")
    @ConstraintsAnnotations.Constrain(value = MaxArgsConstraint.class, type = ConstraintType.INT, integer = 2)
    @ConstraintsAnnotations.Constrain(value = PermsConstraint.class, type = ConstraintType.STRING, str = "minecraft.help")
    public void say(String label, CommandSource source, String[] args) {
        if (args.length == 1) {
            try {
                int page = Integer.parseInt(args[0]);
                this.help(page, source);
            } catch (NumberFormatException e) {
                if (args[0].equalsIgnoreCase("aliases")) {
                    this.aliases(1, source);
                } else if (args[0].equalsIgnoreCase("trident")) {
                    this.fallback("Trident", 1, source);
                } else if (args[0].equalsIgnoreCase("minecraft")) {
                    this.fallback("Minecraft", 1, source);
                } else {
                    this.search(args[0], 1, source);
                }
            }
        } else if (args.length == 2) {
            try {
                int page = Integer.parseInt(args[1]);

                if (args[0].equalsIgnoreCase("aliases")) {
                    this.aliases(page, source);
                } else if (args[0].equalsIgnoreCase("trident")) {
                    this.fallback("Trident", page, source);
                } else if (args[0].equalsIgnoreCase("minecraft")) {
                    this.fallback("Minecraft", page, source);
                } else {
                    this.search(args[0], page, source);
                }
            } catch (NumberFormatException x) {
                source.sendMessage(ChatComponent.create().setColor(ChatColor.RED).setText("No help for " + args[0] + ' ' + args[1]));
            }
        } else {
            this.help(1, source);
        }
    }

    /**
     * Generates the default help page
     *
     * @param page the page to generate
     * @param source the command source
     */
    private void help(int page, CommandSource source) {
        int max = page * PAGE_SIZE;
        int ceil = (int) Math.ceil(TridentServer.getInstance().getCommandHandler().getCmdCount() / (double) PAGE_SIZE);

        if (max <= 0 || page > ceil) {
            source.sendMessage(ChatComponent.create().setColor(ChatColor.RED)
                    .setText("Help page must be between 0 and " + ceil));
            return;
        }

        int it = 0;
        List<String> help = new ArrayList<>();
        Set<CommandDispatcher> dispatcherSet = Collections.newSetFromMap(new IdentityHashMap<>());
        dispatcherSet.addAll(TridentServer.getInstance().getCommandHandler().getDispatchers().values());
        for (CommandDispatcher dispatcher : dispatcherSet) {
            if (it == max) {
                break;
            }

            if (it >= max - 5) {
                String s = ChatColor.GOLD.toString() + '/' + dispatcher.getCommand().name() + ": " +
                        ChatColor.WHITE + dispatcher.getCommand().desc();
                if (s.length() > 64) {
                    s = s.substring(0, 64) + "...";
                }

                help.add(s);
            }

            it++;
        }

        source.sendMessage(ChatComponent.create().setColor(ChatColor.YELLOW).setText("-------- ").
                addExtra(ChatComponent.create().setColor(ChatColor.WHITE).setText("Help: Index (" + page + '/' + ceil + ')')).
                addExtra(ChatComponent.create().setColor(ChatColor.YELLOW).setText(" ----------------------")));
        source.sendMessage(ChatComponent.create().setColor(ChatColor.GRAY).setText("Use /help [n] to get page n of help"));
        source.sendMessage(ChatComponent.create().setColor(ChatColor.GOLD).setText("Aliases: ").
                addExtra(ChatComponent.create().setColor(ChatColor.WHITE).setText("Lists command aliases")));
        source.sendMessage(ChatComponent.create().setColor(ChatColor.GOLD).setText("Trident: ").
                addExtra(ChatComponent.create().setColor(ChatColor.WHITE).setText("All commands for Trident")));
        source.sendMessage(ChatComponent.create().setColor(ChatColor.GOLD).setText("Minecraft: ").
                addExtra(ChatComponent.create().setColor(ChatColor.WHITE).setText("All commands for Minecraft")));
        for (String s : help) {
            source.sendMessage(ChatComponent.fromFormat(s));
        }
    }

    /**
     * Searches for a given command.
     *
     * @param search the search query
     * @param page the page to find
     * @param source the command source
     */
    private void search(String search, int page, CommandSource source) {
        int max = page * PAGE_SIZE;

        if (max <= 0) {
            source.sendMessage(ChatComponent.create().setColor(ChatColor.RED)
                    .setText("Help page must be greater than 0"));
            return;
        }

        int it = 0;
        List<String> help = new ArrayList<>();
        Map<String, CommandDispatcher> dispatchers = TridentServer.getInstance().getCommandHandler().getDispatchers();
        CommandDispatcher dispatcher = dispatchers.get(search);
        if (dispatcher != null) {
            source.sendMessage(ChatComponent.create().setColor(ChatColor.YELLOW).setText("-------- ").
                    addExtra(ChatComponent.create().setColor(ChatColor.WHITE).setText("Help: /" + search)).
                    addExtra(ChatComponent.create().setColor(ChatColor.YELLOW).setText(" ----------------------")));
            if (!dispatcher.getCommand().name().equalsIgnoreCase(search)) {
                source.sendMessage(ChatComponent.create().setColor(ChatColor.YELLOW).setText("Alias for ").
                        addExtra(ChatComponent.create().setColor(ChatColor.WHITE).setText('/' + dispatcher.getCommand().name())));
            }

            source.sendMessage(ChatComponent.create().setColor(ChatColor.GOLD).setText("Description: ").
                    addExtra(ChatComponent.create().setColor(ChatColor.WHITE).setText(dispatcher.getCommand().desc())));
            source.sendMessage(ChatComponent.create().setColor(ChatColor.GOLD).setText("Usage: ").
                    addExtra(ChatComponent.create().setColor(ChatColor.WHITE).setText(dispatcher.getCommand().help())));

            if (dispatcher.getCommand().aliases().length > 0) {
                StringBuilder builder = new StringBuilder();
                for (String alias : dispatcher.getCommand().aliases()) {
                    builder.append(alias).append(", ");
                }
                String text = builder.toString();

                source.sendMessage(ChatComponent.create().setColor(ChatColor.GOLD).setText("Aliases: ").
                        addExtra(ChatComponent.create().setColor(ChatColor.WHITE).setText(text.substring(0, text.length() - 2))));
            }
        } else {
            for (Map.Entry<String, CommandDispatcher> entry : dispatchers.entrySet()) {
                if (this.isSimilar(search, entry.getKey())) {
                    if (it >= max) {
                        it++;
                        continue;
                    }

                    if (it >= max - 5) {
                        if (dispatcher.getCommand().name().equalsIgnoreCase(search)) {
                            String s = ChatColor.GOLD.toString() + '/' + entry.getKey() + ": " +
                                    ChatColor.WHITE + entry.getValue().getCommand().desc();
                            if (s.length() > 64) {
                                s = s.substring(0, 64) + "...";
                            }
                            help.add(s);
                        } else {
                            String s = ChatColor.GOLD.toString() + '/' + entry.getKey() + ": " +
                                    ChatColor.YELLOW + "Alias for " +
                                    ChatColor.WHITE + '/' + entry.getValue().getCommand().name();
                            if (s.length() > 66) {
                                s = s.substring(0, 66) + "...";
                            }

                            help.add(s);
                        }
                    }

                    it++;
                }
            }

            int ceil = Math.max(1, (int) Math.ceil(it / (double) PAGE_SIZE));
            if (page > ceil) {
                source.sendMessage(ChatComponent.create().setColor(ChatColor.RED)
                        .setText("Help page must be between 0 and " + ceil));
                return;
            }

            source.sendMessage(ChatComponent.create().setColor(ChatColor.YELLOW).setText("-------- ").
                    addExtra(ChatComponent.create().setColor(ChatColor.WHITE).setText("Help: Search (" + page + '/' +
                            ceil + ')')).
                    addExtra(ChatComponent.create().setColor(ChatColor.YELLOW).setText(" ----------------------")));
            source.sendMessage(ChatComponent.create().setColor(ChatColor.GRAY).setText("Search for: " + search));
            for (String s : help) {
                source.sendMessage(ChatComponent.fromFormat(s));
            }
        }
    }

    /**
     * Checks to see if two strings match the criteria that
     * one starts with the second at least 70% of the way.
     *
     * @param one the string to check for similarity
     * @param two the "standard" string to check against
     * @return {@code true} if they are similar
     */
    private boolean isSimilar(String one, String two) {
        int times = 0;
        for (int i = 0; i <= one.length(); i++) {
            if (two.contains(one.substring(0, i))) {
                times++;
            }
        }

        return times / ((double) one.length() + 1) > 0.8;
    }

    /**
     * Searches for aliases.
     *
     * @param page the page to generate
     * @param source the command source
     */
    private void aliases(int page, CommandSource source) {
        int max = page * PAGE_SIZE;
        int ceil = (int) Math.ceil((TridentServer.getInstance().getCommandHandler().getDispatchers().size() -
                TridentServer.getInstance().getCommandHandler().getCmdCount()) / (double) PAGE_SIZE);

        if (max <= 0 || page > ceil) {
            source.sendMessage(ChatComponent.create().setColor(ChatColor.RED)
                    .setText("Help page must be between 0 and " + ceil));
            return;
        }

        int it = 0;
        List<String> help = new ArrayList<>();
        for (Map.Entry<String, CommandDispatcher> entry : TridentServer.getInstance().getCommandHandler().getDispatchers().entrySet()) {
            CommandDispatcher dispatcher = entry.getValue();
            if (!dispatcher.getCommand().name().equalsIgnoreCase(entry.getKey())) {
                if (it == max) {
                    break;
                }

                if (it >= max - 5) {
                    String s = ChatColor.GOLD.toString() + '/' + entry.getKey() + ": " +
                            ChatColor.YELLOW + "Alias for " +
                            ChatColor.WHITE + '/' + dispatcher.getCommand().name();
                    if (s.length() > 66) {
                        s = s.substring(0, 66) + "...";
                    }

                    help.add(s);
                }

                it++;
            }
        }

        source.sendMessage(ChatComponent.create().setColor(ChatColor.YELLOW).setText("-------- ").
                addExtra(ChatComponent.create().setColor(ChatColor.WHITE).setText("Help: Aliases (" + page + '/' + ceil + ')')).
                addExtra(ChatComponent.create().setColor(ChatColor.YELLOW).setText(" ----------------------")));
        for (String s : help) {
            source.sendMessage(ChatComponent.fromFormat(s));
        }
    }

    /**
     * Searches for commands that are dispatched to the
     * given fallback string, i.e. their owners.
     *
     * @param f the fallback string to look for
     * @param page the page to generate
     * @param source the command source
     */
    private void fallback(String f, int page, CommandSource source) {
        int max = page * PAGE_SIZE;

        if (max <= 0) {
            source.sendMessage(ChatComponent.create().setColor(ChatColor.RED)
                    .setText("Help page must be greater than 0"));
            return;
        }

        int it = 0;
        List<String> help = new ArrayList<>();
        for (Map.Entry<String, CommandDispatcher> entry : TridentServer.getInstance().getCommandHandler().getDispatchers().entrySet()) {
            CommandDispatcher dispatcher = entry.getValue();
            if (dispatcher.getFallback().equalsIgnoreCase(f) && !dispatcher.getCommand().name().equalsIgnoreCase(entry.getKey())) {
                if (it >= max) {
                    it++;
                    continue;
                }

                if (it >= max - 5) {
                    String s = ChatColor.GOLD.toString() + '/' + entry.getKey() + ": " +
                            ChatColor.WHITE + dispatcher.getCommand().desc();
                    if (s.length() > 64) {
                        s = s.substring(0, 64) + "...";
                    }

                    help.add(s);
                }

                it++;
            }
        }

        int ceil = (int) Math.max(1, Math.ceil(it / (double) PAGE_SIZE));
        if (page > ceil) {
            source.sendMessage(ChatComponent.create().setColor(ChatColor.RED)
                    .setText("Help page must be between 0 and " + ceil));
            return;
        }

        source.sendMessage(ChatComponent.create().setColor(ChatColor.YELLOW).setText("-------- ").
                addExtra(ChatComponent.create().setColor(ChatColor.WHITE).setText("Help: " + f + " (" + page + '/' +
                        ceil + ')')).
                addExtra(ChatComponent.create().setColor(ChatColor.YELLOW).setText(" ----------------------")));
        source.sendMessage(ChatComponent.create().setColor(ChatColor.GRAY).setText("Below is a list of all " + f + " commands:"));
        for (String s : help) {
            source.sendMessage(ChatComponent.fromFormat(s));
        }
    }
}
