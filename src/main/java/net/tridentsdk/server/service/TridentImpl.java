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
package net.tridentsdk.server.service;

import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.ImmutableMap;
import net.tridentsdk.concurrent.Scheduler;
import net.tridentsdk.concurrent.SelectableThreadPool;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.event.Events;
import net.tridentsdk.inventory.Inventories;
import net.tridentsdk.inventory.crafting.RecipeManager;
import net.tridentsdk.meta.component.MetaProvider;
import net.tridentsdk.plugin.Plugins;
import net.tridentsdk.plugin.channel.PluginChannels;
import net.tridentsdk.plugin.cmd.Commands;
import net.tridentsdk.registry.Implementation;
import net.tridentsdk.registry.PlayerStatus;
import net.tridentsdk.registry.Players;
import net.tridentsdk.server.concurrent.ConcurrentTaskExecutor;
import net.tridentsdk.server.concurrent.TridentTaskScheduler;
import net.tridentsdk.server.crafting.TridentRecipeManager;
import net.tridentsdk.server.data.MetaProviderFactory;
import net.tridentsdk.server.event.EventHandler;
import net.tridentsdk.server.inventory.TridentInventories;
import net.tridentsdk.server.packets.play.out.PacketPlayOutPluginMessage;
import net.tridentsdk.server.player.OfflinePlayer;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.plugin.CommandHandler;
import net.tridentsdk.server.plugin.PluginHandler;
import net.tridentsdk.server.world.TridentWorldLoader;
import net.tridentsdk.server.world.change.DefaultMassChange;
import net.tridentsdk.service.ChatFormatter;
import net.tridentsdk.service.Transactions;
import net.tridentsdk.world.MassChange;
import net.tridentsdk.world.World;
import net.tridentsdk.world.WorldLoader;
import net.tridentsdk.world.gen.ChunkGenerator;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * Entry point for plugin API implementations
 *
 * @author The TridentSDK Team
 */
public class TridentImpl implements Implementation {
    private final Scheduler scheduler = TridentTaskScheduler.create();
    private final PluginChannels channelHandler = new PluginChannels() {
        @Override
        public void sendPluginMessage(String channel, byte... data) {
            TridentPlayer.sendAll(new PacketPlayOutPluginMessage().set("channel", channel).set("data", data));
        }
    };
    private final Inventories windowHandler = new TridentInventories();
    private final Events events = EventHandler.create();
    private final PlayerStatus status = new Statuses();
    private final Plugins plugins = new PluginHandler();
    private final Commands commands = new CommandHandler();
    private final ChatFormatter formatter = new ChatHandler();
    private final Transactions trasacts = new TransactionHandler();
    private final MetaProvider metaProviderFactory = new MetaProviderFactory();
    private final TridentRecipeManager recipes = new TridentRecipeManager();

    class PlayersImpl extends ForwardingCollection<Player> implements Players {
        @Override
        protected Collection<Player> delegate() {
            return TridentPlayer.players();
        }

        @Override
        public Player fromUuid(UUID uuid) {
            return TridentPlayer.getPlayer(uuid);
        }

        @Override
        public Player offline(UUID uuid) {
            return OfflinePlayer.getOfflinePlayer(uuid);
        }
    }

    private final Players players = new PlayersImpl();

    @Override
    public SelectableThreadPool newPool(int i, String s) {
        return ConcurrentTaskExecutor.create(i, s);
    }

    @Override
    public WorldLoader newLoader(Class<? extends ChunkGenerator> g) {
        if (g == null) {
            return new TridentWorldLoader();
        }

        return new TridentWorldLoader(g);
    }

    @Override
    public MassChange newMc(World world) {
        return new DefaultMassChange(world);
    }

    @Override
    public Map<String, World> worlds() {
        return ImmutableMap.copyOf(TridentWorldLoader.WORLDS);
    }

    @Override
    public MetaProvider meta() {
        return metaProviderFactory;
    }

    @Override
    public Transactions trasacts() {
        return trasacts;
    }

    @Override
    public ChatFormatter format() {
        return formatter;
    }

    @Override
    public Players players() {
        return players;
    }

    @Override
    public PlayerStatus statuses() {
        return status;
    }

    @Override
    public Events events() {
        return events;
    }

    @Override
    public Plugins plugins() {
        return plugins;
    }

    @Override
    public Scheduler scheduler() {
        return scheduler;
    }

    @Override
    public PluginChannels channels() {
        return channelHandler;
    }

    @Override
    public Commands cmds() {
        return commands;
    }

    @Override
    public Inventories inventories() {
        return windowHandler;
    }

    @Override
    public RecipeManager recipe() {
        return recipes;
    }
}
