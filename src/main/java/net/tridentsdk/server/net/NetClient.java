/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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

import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import net.tridentsdk.server.packet.PacketOut;
import net.tridentsdk.server.packet.login.LoginOutCompression;
import net.tridentsdk.server.packet.login.LoginOutDisconnect;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Map;

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
     * The mapping of currently connected clients
     */
    private static final Map<ChannelHandlerContext, NetClient> CLIENTS =
            Maps.newConcurrentMap();

    /**
     * The channel context which represents the client
     * connection to the server.
     */
    private final ChannelHandlerContext ctx;
    /**
     * The current state of the client connection to the
     * server.
     */
    private volatile NetState currentState;
    /**
     * The name of the player that represents this client
     */
    private volatile String name;
    /**
     * The crypto module used for encrpyting and decrypting
     * server messages.
     */
    private volatile NetCrypto cryptoModule;
    /**
     * Whether or not the client performs compression
     */
    private volatile boolean doCompression;

    /**
     * Creates a new netclient that represents a client's
     * connection to the server.
     */
    public NetClient(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.currentState = NetState.HANDSHAKE;
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
        return CLIENTS.computeIfAbsent(ctx, (k) -> new NetClient(ctx));
    }

    /**
     * Obtains the current state which the client's
     * connection to the server is in.
     *
     * @return the network state
     */
    public NetState state() {
        return this.currentState;
    }

    /**
     * Sets the current state of the client to the given
     * next.
     *
     * @param next the next state
     */
    public void setState(NetState next) {
        this.currentState = next;
    }

    /**
     * Obtains the name of the player, if it exists.
     *
     * @return the name presented upon login
     */
    public String name() {
        return this.name;
    }

    /**
     * Sets the name of the client upon login.
     *
     * @param name the player name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtains the crypto module that is used for
     * encryption
     * and decryption of packets.
     *
     * @return the crypto module
     */
    public NetCrypto cryptoModule() {
        return this.cryptoModule;
    }

    /**
     * Initializes the crypto module and returns the result
     * of doing so.
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
        this.sendPacket(new LoginOutCompression());
        this.doCompression = true;
    }

    /**
     * Sends the given packet to the client's channel.
     *
     * @param packet the packet to send
     */
    public ChannelFuture sendPacket(PacketOut packet) {
        Channel c = this.ctx.channel();
        return c.writeAndFlush(packet);
    }

    /**
     * Disconnects this client from the server.
     *
     * @param reason the reason for disconnecting
     */
    public void disconnect(String reason) {
        if (this.currentState.ordinal() > NetState.STATUS.ordinal()) {
            this.sendPacket(new LoginOutDisconnect(reason)).awaitUninterruptibly();
        }

        this.ctx.close();
    }
}