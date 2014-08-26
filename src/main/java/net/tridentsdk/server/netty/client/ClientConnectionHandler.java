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

import java.net.InetSocketAddress;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import javax.annotation.concurrent.ThreadSafe;

import net.tridentsdk.api.Trident;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketData;
import net.tridentsdk.server.netty.packet.PacketDecoder;
import net.tridentsdk.server.netty.packet.PacketType;
import net.tridentsdk.server.netty.protocol.Protocol;

/**
 * The channel handler that is placed into the netty connection bootstrap to process inbound messages from clients (not just players)
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class ClientConnectionHandler extends SimpleChannelInboundHandler<PacketData> {
    final   Protocol protocol;
    
    public ClientConnectionHandler() {
        super();
        this.protocol = ((TridentServer) Trident.getServer()).getProtocol();
    }
    
    /* (non-Javadoc)
     * @see io.netty.channel.SimpleChannelInboundHandler#messageReceived(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    /**
     * Converts the PacketData to a Packet depending on the ConnectionStage of the Client
     */
    @Override
    protected void messageReceived(ChannelHandlerContext context, PacketData data)
            throws Exception {
        InetSocketAddress address = (InetSocketAddress) context.channel().remoteAddress();
        ClientConnection connection = ClientConnection.getConnection(address);

        if (connection == null) {
            connection = new ClientConnection(context);
        }

        Packet packet = this.protocol.getPacket(data.getId(), connection.getStage(), PacketType.IN);

        //If packet is unknown disconnect the client, as said client seems to be modified
        if (packet.getId() == -1) {
            connection.logout();

            // TODO Print client info. stating that has sent an invalid packet and has been disconnected
            return;
        }

        packet.decode(data.getData());
        
    }
}
