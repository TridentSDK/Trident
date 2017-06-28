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
package net.tridentsdk.server;

import net.tridentsdk.Impl;
import net.tridentsdk.Server;
import net.tridentsdk.base.Substance;
import net.tridentsdk.config.Config;
import net.tridentsdk.doc.Policy;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.inventory.Inventory;
import net.tridentsdk.inventory.InventoryType;
import net.tridentsdk.inventory.Item;
import net.tridentsdk.logger.LogHandler;
import net.tridentsdk.logger.Logger;
import net.tridentsdk.meta.ItemMeta;
import net.tridentsdk.plugin.channel.PluginChannel;
import net.tridentsdk.plugin.channel.SimpleChannelListener;
import net.tridentsdk.server.config.TridentConfig;
import net.tridentsdk.server.inventory.TridentInventory;
import net.tridentsdk.server.inventory.TridentItem;
import net.tridentsdk.server.logger.InfoLogger;
import net.tridentsdk.server.logger.LoggerHandlers;
import net.tridentsdk.server.logger.PipelinedLogger;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.plugin.TridentPluginAllChannel;
import net.tridentsdk.server.plugin.TridentPluginChannel;
import net.tridentsdk.server.ui.bossbar.CustomBossBar;
import net.tridentsdk.server.ui.tablist.TridentCustomTabList;
import net.tridentsdk.server.ui.tablist.TridentGlobalTabList;
import net.tridentsdk.server.ui.title.CustomTitle;
import net.tridentsdk.server.util.Debug;
import net.tridentsdk.ui.bossbar.BossBar;
import net.tridentsdk.ui.tablist.TabList;
import net.tridentsdk.ui.title.Title;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * This class is the bridge between the server and the API,
 * and provides the implementation classes for the API via
 * {@link Impl}.
 */
@Immutable
public class ImplementationProvider implements Impl.ImplementationProvider {
    // head of the logger pipeline
    private final PipelinedLogger head;
    // instance of the handlers class
    private final LoggerHandlers handlers;

    public ImplementationProvider(PipelinedLogger head) {
        this.head = head;

        for (PipelinedLogger logger = head; logger.next() != null; logger = logger.next()) {
            if (logger.getClass().equals(LoggerHandlers.class)) {
                this.handlers = (LoggerHandlers) logger;
                return;
            }
        }
        throw new IllegalStateException("No handler found");
    }

    @Override
    public Server getServer() {
        return TridentServer.getInstance();
    }

    @Override
    public Config newCfg(Path p) {
        return TridentConfig.load(p);
    }

    @Override
    public Logger newLogger(String s) {
        return InfoLogger.get(this.head, s);
    }

    @Override
    public void attachHandler(Logger logger, LogHandler handler) {
        if (logger == null) {
            this.handlers.handlers().add(handler);
        } else {
            InfoLogger info = (InfoLogger) logger;
            info.handlers().add(handler);
        }
    }

    @Override
    public boolean removeHandler(Logger logger, LogHandler handler) {
        if (logger == null) {
            return this.handlers.handlers().remove(handler);
        } else {
            InfoLogger info = (InfoLogger) logger;
            return info.handlers().remove(handler);
        }
    }

    @Override
    public TabList getGlobalTabList() {
        return TridentGlobalTabList.getInstance();
    }

    @Override
    public TabList newTabList() {
        return new TridentCustomTabList();
    }

    @Override
    public BossBar newBossBar() {
        return new CustomBossBar();
    }

    @Override
    public Title newTitle() {
        return new CustomTitle();
    }

    @Override
    public Inventory newInventory(InventoryType type, int slots) {
        return new TridentInventory(type, slots);
    }

    @Override
    public Item newItem(Substance substance, int count, byte damage, ItemMeta meta) {
        return new TridentItem(substance, count, damage, meta);
    }

    @Override
    @Nonnull
    public Map<String, Player> findByName(String name) {
        // Skip list orders first from numbers, then
        // 1A to 16A then 1a to 16a, then 1B to 16B then
        // to 1b to 16b
        // _ goes last
        String top = TridentPlayer.getPlayerNames().ceilingKey(name.toUpperCase());

        StringBuilder last = new StringBuilder(name);
        for (int i = 0; i < 16 - name.length(); i++) {
            last.append('_');
        }
        String bot = TridentPlayer.getPlayerNames().floorKey(last.toString());

        // Nothing in between, therefore no possibilities
        if (top == null || bot == null) {
            return Collections.emptyMap();
        }

        if (top.equals(bot)) {
            return Collections.singletonMap(top, TridentPlayer.getPlayerNames().get(top));
        }

        return Collections.unmodifiableMap(TridentPlayer.getPlayerNames().subMap(top, true, bot, true));
    }

    @Override
    @Nullable
    public Player getByName(String name) {
        return TridentPlayer.getPlayerNames().get(name);
    }

    @Override
    public PluginChannel open(String name, Player... targets) {
        PluginChannel channel = TridentPluginChannel.getChannel(name, TridentPluginChannel::new);
        channel.addRecipient(targets);

        return channel;
    }

    @Override
    public PluginChannel open(String name, Collection<? extends Player> players) {
        PluginChannel channel = TridentPluginChannel.getChannel(name, TridentPluginChannel::new);
        channel.addRecipient(players);

        return channel;
    }

    @Override
    public PluginChannel openAll(String name) {
        return TridentPluginChannel.getChannel(name, TridentPluginAllChannel::new);
    }

    @Override
    public PluginChannel tryOpen(String name) {
        Map.Entry<String, Player> entry = TridentPlayer.getPlayerNames().firstEntry();
        if (entry == null) {
            return null;
        } else {
            return this.open(name, entry.getValue());
        }
    }

    @Override
    public PluginChannel get(String name) {
        return TridentPluginChannel.get(name);
    }

    @Override
    @Policy("plugin thread only")
    public void register(SimpleChannelListener listener) {
        Debug.tryCheckThread();
        TridentPluginChannel.register(listener);
    }

    @Override
    @Policy("plugin thread only")
    public boolean unregister(Class<? extends SimpleChannelListener> cls) {
        Debug.tryCheckThread();
        return TridentPluginChannel.unregister(cls);
    }
}