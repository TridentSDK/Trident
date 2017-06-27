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
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.plugin.channel.Destination;
import net.tridentsdk.plugin.channel.PluginChannel;
import net.tridentsdk.plugin.channel.SimpleChannelListener;
import net.tridentsdk.server.net.NetData;
import net.tridentsdk.server.packet.play.PlayOutPluginMsg;
import net.tridentsdk.server.player.TridentPlayer;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Implements a plugin channel.
 */
@ThreadSafe
public class TridentPluginChannel implements PluginChannel {
    /**
     * Cache of open plugin channels.
     */
    private static final Map<String, TridentPluginChannel> CHANNELS = new ConcurrentHashMap<>();
    /**
     * Mapping of all the listener entries
     */
    @Getter
    private static final Map<Class<? extends SimpleChannelListener>, SimpleChannelListener> listeners =
            new ConcurrentHashMap<>();

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
     * Whether or not this channel sends its messages to all
     * players
     */
    private final boolean forAll;

    /**
     * Whether or not this channel is closed
     */
    private volatile boolean closed;

    public TridentPluginChannel(String name, Player... targets) {
        this.name = name;
        this.forAll = false;

        PlayOutPluginMsg msg = new PlayOutPluginMsg(REGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : targets) {
            this.recipients.put(player.getUuid(), player);
            ((TridentPlayer) player).net().sendPacket(msg);
        }
    }

    public TridentPluginChannel(String name, Iterable<Player> players) {
        this.name = name;
        this.forAll = false;

        PlayOutPluginMsg msg = new PlayOutPluginMsg(REGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : players) {
            this.recipients.put(player.getUuid(), player);
            ((TridentPlayer) player).net().sendPacket(msg);
        }
    }

    public TridentPluginChannel(String name) {
        this.name = name;
        this.forAll = true;
    }

    /**
     * Cache lookup and compute for the plugin channel with
     * the given name.
     *
     * @param name the name of the channel
     * @param func the compute function
     * @return the old or new channel, depending if it
     * existed or not
     */
    public static TridentPluginChannel getChannel(String name, Function<String, TridentPluginChannel> func) {
        return CHANNELS.computeIfAbsent(name, func);
    }

    /**
     * Gets a channel by name from the cache.
     *
     * @param name the name of the channel
     * @return the channel, or {@code null} if it is not
     * cached
     */
    public static TridentPluginChannel get(String name) {
        return CHANNELS.get(name);
    }

    /**
     * Registers the given listener with the listener
     * mappings.
     *
     * @param listener the listener to register
     */
    public static void register(SimpleChannelListener listener) {
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
    public static boolean unregister(Class<? extends SimpleChannelListener> cls) {
        return listeners.remove(cls) != null;
    }

    @Override
    public void close() {
        this.closed = true;
        CHANNELS.remove(this.name);

        PlayOutPluginMsg msg = new PlayOutPluginMsg(UNREGISTER, this.name.getBytes(NetData.NET_CHARSET));
        Collection<? extends Player> players = this.forAll ? TridentPlayer.getPlayers().values() : this.recipients.values();
        for (Player player : players) {
            if (!this.forAll) {
                this.recipients.remove(player.getUuid());
            }

            this.recipients.remove(player.getUuid());
            ((TridentPlayer) player).net().sendPacket(msg);
        }

        for (SimpleChannelListener listener : listeners.values()) {
            listener.channelClosed(this, Destination.CLIENT, players);
        }
    }

    @Override
    public void closeFor(Function<Player, Boolean> function) {
        PlayOutPluginMsg msg = new PlayOutPluginMsg(UNREGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : this.forAll ? TridentPlayer.getPlayers().values() : this.recipients.values()) {
            if (function.apply(player)) {
                if (!this.forAll) {
                    this.recipients.remove(player.getUuid());
                }

                ((TridentPlayer) player).net().sendPacket(msg);
            }
        }
    }

    @Override
    public boolean closeFor(Iterable<Player> players) {
        boolean success = true;
        PlayOutPluginMsg msg = new PlayOutPluginMsg(UNREGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : players) {
            if (!this.forAll) {
                this.recipients.remove(player.getUuid());
            }

            if (player != null) {
                ((TridentPlayer) player).net().sendPacket(msg);
            } else {
                success = false;
            }
        }

        return success;
    }

    @Override
    public boolean closeFor(UUID... uuids) {
        boolean success = true;
        PlayOutPluginMsg msg = new PlayOutPluginMsg(UNREGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (UUID uuid : uuids) {
            Player player;
            if (this.forAll) {
                player = TridentPlayer.getPlayers().get(uuid);
            } else {
                player = this.recipients.remove(uuid);
            }

            if (player != null) {
                ((TridentPlayer) player).net().sendPacket(msg);
            } else {
                success = false;
            }
        }

        return success;
    }

    @Override
    public void addRecipient(Player... recipients) {
        if (this.forAll) {
            throw new UnsupportedOperationException("All players have been registered");
        }

        PlayOutPluginMsg msg = new PlayOutPluginMsg(REGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (Player player : recipients) {
            this.recipients.put(player.getUuid(), player);
            ((TridentPlayer) player).net().sendPacket(msg);
        }
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

        for (SimpleChannelListener listener : TridentPluginChannel.getListeners().values()) {
            listener.messageSent(this, message);
        }

        PlayOutPluginMsg msg = new PlayOutPluginMsg(this.name, message);
        for (Player player : this.forAll ? TridentPlayer.getPlayers().values() : this.recipients.values()) {
            ((TridentPlayer) player).net().sendPacket(msg);
        }
        return true;
    }
}