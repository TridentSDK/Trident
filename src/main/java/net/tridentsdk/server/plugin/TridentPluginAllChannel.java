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
import net.tridentsdk.entity.living.EntityPlayer;
import net.tridentsdk.plugin.channel.Destination;
import net.tridentsdk.plugin.channel.PluginChannel;
import net.tridentsdk.plugin.channel.SimpleChannelListener;
import net.tridentsdk.server.concurrent.PoolSpec;
import net.tridentsdk.server.concurrent.ServerThreadPool;
import net.tridentsdk.server.net.NetData;
import net.tridentsdk.server.packet.play.PlayOutPluginMsg;
import net.tridentsdk.server.player.TridentPlayer;

import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.util.function.Function;

/**
 * Implementing a plugin channel for all players on a
 * server.
 */
@ThreadSafe
@RequiredArgsConstructor
public class TridentPluginAllChannel implements PluginChannel {
    /**
     * The channel name
     */
    @Getter
    private final String name;

    /**
     * Whether or not this channel is closed
     */
    private volatile boolean closed;

    @Override
    public void close() {
        this.closed = true;
        TridentPluginChannel.remove(this.name);

        PlayOutPluginMsg msg = new PlayOutPluginMsg(TridentPluginChannel.UNREGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (EntityPlayer player : TridentPlayer.getPlayers().values()) {
            ((TridentPlayer) player).net().sendPacket(msg);
        }

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : TridentPluginChannel.getListeners().values()) {
                listener.channelClosed(this, Destination.CLIENT, TridentPlayer.getPlayers().values());
            }
        });
    }

    @Override
    public void closeFor(Function<EntityPlayer, Boolean> function) {
        Set<EntityPlayer> players = new HashSet<>();
        PlayOutPluginMsg msg = new PlayOutPluginMsg(TridentPluginChannel.UNREGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (EntityPlayer player :TridentPlayer.getPlayers().values()) {
            if (function.apply(player)) {
                players.add(player);
                ((TridentPlayer) player).net().sendPacket(msg);
            }
        }

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener :  TridentPluginChannel.getListeners().values()) {
                listener.channelClosed(this, Destination.CLIENT, players);
            }
        });
    }

    @Override
    public boolean closeFor(Collection<EntityPlayer> players) {
        PlayOutPluginMsg msg = new PlayOutPluginMsg(TridentPluginChannel.UNREGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (EntityPlayer player : players) {
            ((TridentPlayer) player).net().sendPacket(msg);
        }

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener :  TridentPluginChannel.getListeners().values()) {
                listener.channelClosed(this, Destination.CLIENT, players);
            }
        });

        return true;
    }

    @Override
    public boolean closeFor(UUID... uuids) {
        boolean success = true;
        Set<EntityPlayer> players = new HashSet<>();
        PlayOutPluginMsg msg = new PlayOutPluginMsg(TridentPluginChannel.UNREGISTER, this.name.getBytes(NetData.NET_CHARSET));
        for (UUID uuid : uuids) {
            EntityPlayer player = TridentPlayer.getPlayers().get(uuid);
            if (player != null) {
                players.add(player);
                ((TridentPlayer) player).net().sendPacket(msg);
            } else {
                success = false;
            }
        }

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener :  TridentPluginChannel.getListeners().values()) {
                listener.channelClosed(this, Destination.CLIENT, players);
            }
        });

        return success;
    }

    @Override
    public void addRecipient(EntityPlayer... recipients) {
        throw new UnsupportedOperationException("Cannot add players: all players are in this channel");
    }

    @Override
    public void addRecipient(Collection<? extends EntityPlayer> recipients) {
        throw new UnsupportedOperationException("Cannot add players: all players are in this channel");
    }

    @Override
    public Collection<EntityPlayer> getRecipients() {
        return Collections.unmodifiableCollection(TridentPlayer.getPlayers().values());
    }

    @Override
    public boolean send(byte[] message) {
        if (this.closed) {
            return false;
        }

        PlayOutPluginMsg msg = new PlayOutPluginMsg(this.name, message);
        for (EntityPlayer player : TridentPlayer.getPlayers().values()) {
            ((TridentPlayer) player).net().sendPacket(msg);
        }

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            for (SimpleChannelListener listener : TridentPluginChannel.getListeners().values()) {
                listener.messageSent(this, message);
            }
        });
        return true;
    }
}
