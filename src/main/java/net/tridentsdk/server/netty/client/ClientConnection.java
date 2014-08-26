/*
 * Copyright (C) 2014 The TridentSDK Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theplate
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tridentsdk.server.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.protocol.Protocol;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles the connection of a client upon joining
 * 
 * @author The TridentSDK Team
 */ 
public class ClientConnection {
    // TODO: Find a more efficient way to do this
    private static final Map<InetSocketAddress, ClientConnection> clientData =
            new ConcurrentHashMap<>();

    private final    InetSocketAddress    address;
    private final    Channel              channel;
    private volatile Protocol.ClientStage stage;

    /**
     * Creates a new connection handler for the joining channel stream
     * 
     * @param channelContext the channel of the client joining
     */ 
    public ClientConnection(ChannelHandlerContext channelContext) {
        this.address = (InetSocketAddress) channelContext.channel().remoteAddress();
        this.channel = channelContext.channel();

        ClientConnection.clientData.put(this.address, this);
    }

    /**
     * Checks if an IP address is logged into the server
     * 
     * @param address the address to check if online
     * @return {@code true} if the IP is on the server, {@code false} if not
     */ 
    public static boolean isLoggedIn(InetSocketAddress address) {
        return ClientConnection.clientData.containsKey(address);
    }

    /**
     * Gets the connection by the IP address
     * 
     * @param address the IP to lookup the connection handler
     * @return the instnace of the client handler associated with the IP
     */ 
    public static ClientConnection getConnection(InetSocketAddress address) {
        return ClientConnection.clientData.get(address);
    }

    /**
     * Sends protocol data through the client stream
     * 
     * @param packet the packet to send, encoded and written to the stream
     */ 
    public void sendPacket(Packet packet) {
        // Create new ByteBuf
        ByteBuf buffer = this.channel.alloc().buffer();

        //Encode the packet id then the packet
        buffer.writeInt(packet.getId());
        packet.encode(buffer);

        // Write the encoded packet and flush it
        this.channel.writeAndFlush(buffer);

        // In case Channel state changes lets update the HashMap
        ClientConnection.clientData.put(this.address, this);
    }

    /**
     * Gets the channel context for the connection stream
     * 
     * @return the netty channel wrapped by the handler
     */ 
    public Channel getChannel() {
        return this.channel;
    }

    /**
     * The IP address of the client handled by this connection wrapper
     * 
     * @return the handled IP address
     */ 
    public InetSocketAddress getAddress() {
        return this.address;
    }

    /**
     * Gets the current state of the connection
     * 
     * @return the current state of the protocol for the client
     */ 
    public Protocol.ClientStage getStage() {
        return this.stage;
    }

    /**
     * Sets the client state, should only be used by the ClientConnectionHandler
     * 
     * @param stage the state to set the client to
     */ 
    public void setStage(Protocol.ClientStage stage) {
        this.stage = stage;

        ClientConnection.clientData.put(this.address, this);
    }

    /**
     * Removes the client's server side client handler
     */ 
    public void logout() {
        // TODO
        ClientConnection.clientData.remove(this.address);
        this.channel.close();
    }
}
