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
package net.tridentsdk.server.plugin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.tridentsdk.doc.Policy;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.plugin.channel.Destination;
import net.tridentsdk.plugin.channel.PluginChannel;
import net.tridentsdk.plugin.channel.SimpleChannelListener;
import net.tridentsdk.server.concurrent.PoolSpec;
import net.tridentsdk.server.concurrent.ServerThreadPool;
import net.tridentsdk.server.net.NetData;
import net.tridentsdk.server.packet.play.PlayOutPluginMsg;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.util.Debug;

import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Implements a plugin channel.
 */
@ThreadSafe
@RequiredArgsConstructor
public class TridentPluginChannel implements PluginChannel {
    /**
     * Cache of open plugin channels.
     */
    private static final Map<String, PluginChannel> CHANNELS = new ConcurrentHashMap<>();
    /**
     * Mapping of all the listener entries.
     *
     * <p>This map may only be accessed by the plugin
     * thread.</p>
     */
    private static final Map<Class<? extends SimpleChannelListener>, SimpleChannelListener> listeners =
            new HashMap<>();

    /**
     * Obtains the listener map.
     *
     * @return the listener map
     */
    @net.tridentsdk.doc.Debug
    public static Map<Class<? extends SimpleChannelListener>, SimpleChannelListener> getListeners() {
        Debug.tryCheckThread();
        return listeners;
    }

    /**
     * The register channel name
     */
    public static final String REGISTER = "REGISTER";
    /**
     * The unregister channel name
     */
    public static final String UNREGISTER = "UNREGISTER";

    /**
     * The recipients of the plugin messages sent by this
     * channel
     */
    private final Map<UUID, Player> recipients = new ConcurrentHashMap<>();
    /**
     * The channel name
     */
    @Getter
    private final String name;

    /**
     * Whether or not this channel is closed
     */
    private volatile boolean closed;

    /**
     * Cache lookup and compute for the plugin channel with
     * the given name.
     *
     * @param name the name of the channel
     * @param func the compute function
     * @return the old or new channel, depending if it
     * existed or not
     */
    public static PluginChannel getChannel(String name, Function<String, PluginChannel> func) {
        return CHANNELS.computeIfAbsent(name, func);
    }

    /**
     * Gets a channel by name from the cache.
     *
     * @param name the name of the channel
     * @return the channel, or {@code null} if it is not
     * cached
     */
    public static PluginChannel get(String name) {
        return CHANNELS.get(name);
    }

    /**
     * Removes the channel with the given name from the
     * mapping of channels registered.
     *
     * @param name the name of the channel to remove
     */
    public static void remove(String name) {
        CHANNELS.remove(name);
    }

    /**
     * Autoadds the given player to the recipients of all
     * "all" channels
     *
     * @param player the player to add
     */
    public static void autoAdd(TridentPlayer player) {
        Set<Player> singleton = Collections.singleton(player);
        for (PluginChannel channel : CHANNELS.values()) {
            if (channel instanceof TridentPluginAllChannel) {
                PlayOutPluginMsg msg = new PlayOutPluginMsg(REGISTER,
                        channel.getName().getBytes(NetData.NET_CHARSET));
                player.net().sendPacket(msg);

                ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
                    for (SimpleChannelListener listener : listeners.values()) {
                        listener.channelOpened(channel, Destination.CLIENT, singleton);
                    }
                });
            }
        }
    }

    /**
     * Autoremoves the given player from the recipients list
     * of all the "all" channels
     *
     * @param player the player to remove
     */
    public static void autoRemove(TridentPlayer player) {
        Set<Player> singleton = Collections.singleton(player);
        for (PluginChannel channel : CHANNELS.values()) {
            if (channel instanceof TridentPluginAllChannel) {
                channel.closeFor(singleton);

                ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
                    for (SimpleChannelListener listener : listeners.values()) {
                        listener.channelClosed(channel, Destination.CLIENT, singleton);
                    }
                });
            }
        }
    }

    /**
     * Registers the given listener with the listener
     * mappings.
     *
     * @param listener the listener to register
     */
    @Policy("plugin thread only")
    public static void register(SimpleChannelListener listener) {
        Debug.tryCheckThread();
        listeners.put(listener.getClass(), listener);
    }

    /**
     * Removes the listener with the given class from the
     * listener mappings.
     *
     * @param cls the class containing the listener
     * @return {@code true} if the class was successfully
     * unregistered, {@code false} otherwise
     */
    @Policy("plugin thread only")
    public static boolean unregister(Class<? extends SimpleChannelListener> cls) {
        Debug.tryCheckThread();
        return listeners.remove(cls) != null;
    }

    @Override
    public void close() {
        this.closed = true;
        CHANNELS.remove(this.name);

        PlayOutPluginMsg msg = new PlayOutPluginMsg(UNREGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : this.recipients.values()) {
            this.recipients.remove(player.getUuid());
            ((TridentPlayer) player).net().sendPacket(msg);
        }

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : listeners.values()) {
                listener.channelClosed(this, Destination.CLIENT, this.recipients.values());
            }
        });
    }

    @Override
    public void closeFor(Function<Player, Boolean> function) {
        Set<Player> players = new HashSet<>();
        PlayOutPluginMsg msg = new PlayOutPluginMsg(UNREGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : this.recipients.values()) {
            if (function.apply(player)) {
                players.add(player);
                this.recipients.remove(player.getUuid());
                ((TridentPlayer) player).net().sendPacket(msg);
            }
        }

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : listeners.values()) {
                listener.channelClosed(this, Destination.CLIENT, players);
            }
        });
    }

    @Override
    public boolean closeFor(Collection<Player> players) {
        boolean success = true;
        PlayOutPluginMsg msg = new PlayOutPluginMsg(UNREGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : players) {
            player = this.recipients.remove(player.getUuid());
            if (player != null) {
                ((TridentPlayer) player).net().sendPacket(msg);
            } else {
                success = false;
            }
        }

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : listeners.values()) {
                listener.channelClosed(this, Destination.CLIENT, players);
            }
        });

        return success;
    }

    @Override
    public boolean closeFor(UUID... uuids) {
        boolean success = true;
        Set<Player> players = new HashSet<>();
        PlayOutPluginMsg msg = new PlayOutPluginMsg(UNREGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (UUID uuid : uuids) {
            Player player = this.recipients.remove(uuid);
            if (player != null) {
                players.add(player);
                ((TridentPlayer) player).net().sendPacket(msg);
            } else {
                success = false;
            }
        }

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : listeners.values()) {
                listener.channelClosed(this, Destination.CLIENT, players);
            }
        });

        return success;
    }

    @Override
    public void addRecipient(Player... recipients) {
        Set<Player> players = new HashSet<>();
        PlayOutPluginMsg msg = new PlayOutPluginMsg(REGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : recipients) {
            players.add(player);
            this.recipients.put(player.getUuid(), player);
            ((TridentPlayer) player).net().sendPacket(msg);
        }

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : listeners.values()) {
                listener.channelOpened(this, Destination.CLIENT, players);
            }
        });
    }

    @Override
    public void addRecipient(Collection<? extends Player> recipients) {
        PlayOutPluginMsg msg = new PlayOutPluginMsg(REGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : recipients) {
            this.recipients.put(player.getUuid(), player);
            ((TridentPlayer) player).net().sendPacket(msg);
        }

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : listeners.values()) {
                listener.channelOpened(this, Destination.CLIENT, recipients);
            }
        });
    }

    @Override
    public Collection<Player> getRecipients() {
        return Collections.unmodifiableCollection(this.recipients.values());
    }

    @Override
    public boolean send(byte[] message) {
        if (this.closed) {
            return false;
        }

        PlayOutPluginMsg msg = new PlayOutPluginMsg(this.name, message);
        for (Player player : this.recipients.values()) {
            ((TridentPlayer) player).net().sendPacket(msg);
        }

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : listeners.values()) {
                listener.messageSent(this, message);
            }
        });
        return true;
    }
}