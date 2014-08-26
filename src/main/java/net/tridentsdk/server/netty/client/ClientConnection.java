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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tridentsdk.server.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.tridentsdk.api.docs.AccessNoDoc;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.protocol.Protocol;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AccessNoDoc
class ClientConnection {
    // TODO look over non-public, because there will be loads of crap going if we let people screw with this (easily)
    // TODO: Find a more efficient way to do this
    private static final Map<InetSocketAddress, ClientConnection> clientData =
            new ConcurrentHashMap<>();

    private final    InetSocketAddress    address;
    private final    Channel              channel;
    private volatile Protocol.ClientStage stage;

    public ClientConnection(ChannelHandlerContext channelContext) {
        this.address = (InetSocketAddress) channelContext.channel().remoteAddress();
        this.channel = channelContext.channel();

        ClientConnection.clientData.put(this.address, this);
    }

    public static boolean isLoggedIn(InetSocketAddress address) {
        return ClientConnection.clientData.containsKey(address);
    }

    public static ClientConnection getConnection(InetSocketAddress address) {
        return ClientConnection.clientData.get(address);
    }

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

    public Channel getChannel() {
        return this.channel;
    }

    public InetSocketAddress getAddress() {
        return this.address;
    }

    public Protocol.ClientStage getStage() {
        return this.stage;
    }

    public void setStage(Protocol.ClientStage stage) {
        this.stage = stage;

        ClientConnection.clientData.put(this.address, this);
    }

    public void logout() {
        // TODO
        ClientConnection.clientData.remove(this.address);
        this.channel.close();
    }
}
