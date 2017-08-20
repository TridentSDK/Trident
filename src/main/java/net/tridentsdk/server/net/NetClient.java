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
package net.tridentsdk.server.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import lombok.Getter;
import lombok.Setter;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.packet.PacketOut;
import net.tridentsdk.server.packet.login.LoginOutCompression;
import net.tridentsdk.server.packet.login.LoginOutDisconnect;
import net.tridentsdk.server.packet.play.PlayOutDisconnect;
import net.tridentsdk.server.packet.play.PlayOutKeepAlive;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.ui.chat.ChatComponent;

import javax.annotation.concurrent.ThreadSafe;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class represents the connection that a Minecraft
 * client player has to the server.
 */
@ThreadSafe
public class NetClient {
    /**
     * Represents the current connection state that the
     * client is in whilst connecting to the server.
     */
    public enum NetState {
        /**
         * Handshake, attempting to connect to server.
         */
        HANDSHAKE,
        /**
         * Obtain server status via ping.
         */
        STATUS,
        /**
         * Login, authenticate and complete connection
         * formalities before joining.
         */
        LOGIN,
        /**
         * Normal gameplay.
         */
        PLAY
    }

    /**
     * The buffer size used for compressed and
     * decompression
     * buffering
     */
    public static final int BUFFER_SIZE = 8192;
    /**
     * Time before the server kicks an inactive client, in
     * nanoseconds (30 seconds)
     */
    public static final long KEEP_ALIVE_KICK_NANOS = 30_000_000_000L;
    /**
     * The interval between each client tick to ensure that
     * the client is not disconnected (10 seconds)
     */
    private static final long KEEP_ALIVE_INTERVAL_NANOS = 10_000_000_000L;
    /**
     * The mapping of currently connected clients
     */
    private static final Map<SocketAddress, NetClient> CLIENTS = new ConcurrentHashMap<>();

    /**
     * The channel which is the connection that this client
     * has to the server.
     */
    @Getter
    private final Channel channel;
    /**
     * The current state of the client connection to the
     * server.
     */
    @Getter
    @Setter
    private volatile NetClient.NetState state;
    /**
     * The name of the player that represents this client
     */
    private final AtomicReference<String> name = new AtomicReference<>();
    /**
     * The crypto module used for encrypting and decrypting
     * server messages.
     */
    @Getter
    private volatile NetCrypto cryptoModule;
    /**
     * Whether or not the client performs compression
     */
    private volatile boolean doCompression;
    /**
     * The player object
     */
    @Setter
    @Getter
    private volatile TridentPlayer player;
    /**
     * The time it took for the server to receive the
     * status ping from the player, in ms.
     */
    @Getter
    private final AtomicLong ping = new AtomicLong();

    /**
     * The last time which this player was pinged for keep
     * alive
     */
    private final AtomicLong lastKeepAlive = new AtomicLong(System.nanoTime());

    /**
     * Creates a new netclient that represents a client's
     * connection to the server.
     *
     * @param ctx the context
     */
    public NetClient(ChannelHandlerContext ctx) {
        this.channel = ctx.channel();
        this.state = NetClient.NetState.HANDSHAKE;
        this.channel.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                channelFuture.removeListener(this);
                NetClient.this.disconnect("Player lost connection");
            }
        });
    }

    /**
     * Obtains an instance of a net client from the cache
     * of
     * currently connected clients, or creates a new one if
     * it doesn't exist.
     *
     * @param ctx the connection
     * @return the net client wrapping the given connection
     */
    public static NetClient get(ChannelHandlerContext ctx) {
        return CLIENTS.computeIfAbsent(ctx.channel().remoteAddress(), k -> new NetClient(ctx));
    }

    /**
     * Obtains the last moment (in the time value given by
     * {@link System#nanoTime()}) since the client was sent
     * a keep alive packet.
     *
     * @return the last keep alive
     */
    public long lastKeepAlive() {
        return this.lastKeepAlive.get();
    }

    /**
     * Ticks the client.
     */
    public void tick() {
        long lastKeepAlive = this.lastKeepAlive.get();
        long now = System.nanoTime();
        long elapsed = now - lastKeepAlive;
        if (elapsed > KEEP_ALIVE_INTERVAL_NANOS) {
            // if we win a race, great
            // if we lose a race, sucks, but we don't need
            // to retry because it was too recent
            if (this.lastKeepAlive.compareAndSet(lastKeepAlive, now)) {
                this.sendPacket(new PlayOutKeepAlive(this));
            }
        }
    }

    /**
     * Initializes the crypto module and returns the result
     * of doing so.
     *
     * @return the crypto module
     */
    public NetCrypto initCrypto() {
        return this.cryptoModule = new NetCrypto();
    }

    /**
     * Determines whether or not this Minecraft client will
     * perform compression on messages.
     *
     * @return {@code true} if so
     */
    public boolean doCompression() {
        return this.doCompression;
    }

    /**
     * Enables compression when the client is ready.
     */
    public void enableCompression() {
        if (TridentServer.cfg().compressionThresh() != -1) {
            this.sendPacket(new LoginOutCompression())
                    .addListener(future -> this.doCompression = true);
        }
    }

    /**
     * Sends the given packet to the client's channel.
     *
     * @param packet the packet to send
     * @return the response future
     */
    public ChannelFuture sendPacket(PacketOut packet) {
        return this.channel.writeAndFlush(packet);
    }

    /**
     * Gets the name of the player.
     *
     * @return the player name
     */
    public String getName() {
        return this.name.get();
    }

    /**
     * Sets the player name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name.set(name);
    }

    /**
     * Overload method of {@link #disconnect(ChatComponent)} but
     * uses
     * shortcut String.
     *
     * @param reason the string reason
     */
    public void disconnect(String reason) {
        this.disconnect(ChatComponent.text(reason));
    }

    /**
     * Disconnects this client from the server.
     *
     * @param reason the reason for disconnecting
     */
    public Future<Void> disconnect(ChatComponent reason) {
        String name = this.name.get();
        if (name == null) {
            this.channel.close();
            CLIENTS.remove(this.channel.remoteAddress());
            return null;
        }

        if (name.equals("( ͡° ͜ʖ ͡°)( ͡° ͜ʖ ͡°)( ͡° ͜ʖ ͡°)")) { // pretty well future-proofed name
            return null;
        }

        Future<Void> waiter = null;
        if (this.name.compareAndSet(name, "( ͡° ͜ʖ ͡°)( ͡° ͜ʖ ͡°)( ͡° ͜ʖ ͡°)")) {
            NetClient.NetState state = this.state;

            if (state == NetClient.NetState.LOGIN) {
                waiter = this.sendPacket(new LoginOutDisconnect(reason)).addListener(future -> {
                    this.channel.close();
                    TridentServer.getInstance().getLogger().log("Player " + name + " has disconnected: " + reason.getText());
                });
            } else if (state == NetClient.NetState.PLAY) {
                TridentPlayer player = this.player;
                if (player != null) {
                    waiter = this.sendPacket(new PlayOutDisconnect(reason)).addListener(future -> {
                        this.channel.close();
                        player.remove();
                        TridentServer.getInstance().getLogger().log("Player " + name + " [" + player.getUuid() + "] has disconnected: " + reason.getText());
                    });
                }
            } else if (state == NetState.STATUS) {
                waiter = this.channel.close();
            }

            CLIENTS.remove(this.channel.remoteAddress());
        }
        return waiter;
    }
}