/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.server.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.tridentsdk.api.Trident;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.netty.packet.*;
import net.tridentsdk.server.netty.protocol.Protocol;
import net.tridentsdk.server.threads.BackgroundTaskExecutor;
import net.tridentsdk.server.threads.PlayerThreads;

import javax.annotation.concurrent.ThreadSafe;
import java.net.InetSocketAddress;

/**
 * The channel handler that is placed into the netty connection bootstrap to process inbound messages from clients (not
 * just players)
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class ClientConnectionHandler extends SimpleChannelInboundHandler<PacketData> {
    private final Protocol protocol;

    public ClientConnectionHandler() {
        this.protocol = ((TridentServer) Trident.getServer()).getProtocol();
    }
    
    /*
     * (non-Javadoc)
     * @see io.netty.channel.SimpleChannelInboundHandler#messageReceived(io.netty.channel.ChannelHandlerContext,
     * java.lang.Object)
     */

    /**
     * Converts the PacketData to a Packet depending on the ConnectionStage of the Client
     * <p/>
     * {@inheritDoc}
     */
    @Override
    protected void messageReceived(ChannelHandlerContext context, PacketData data)
            throws Exception {
        InetSocketAddress address = (InetSocketAddress) context.channel().remoteAddress();
        ClientConnection connection = ClientConnection.getConnection(address);

        if (connection == null) {
            connection = ClientConnection.registerConnection(context);
        }
        
        //FIXME
        /*if (connection.isEncryptionEnabled()) {
            data.decrypt(connection);
        }*/

        Packet packet = this.protocol.getPacket(data.getId(), connection.getStage(), PacketType.IN);
        
        //If packet is unknown disconnect the client, as said client seems to be modified
        if (packet.getId() == -1) {
            connection.logout();
            
            // TODO Print client info. stating that has sent an invalid packet and has been disconnected
            return;
        }

        packet.decode(data.getData());

        packet.handleReceived(connection);

        final ClientConnection finalConnection = connection;
        BackgroundTaskExecutor.execute(new Runnable() {
            @Override public void run() {
                PlayerThreads.clientThreadHandle(finalConnection);
            }
        });
    }
}
