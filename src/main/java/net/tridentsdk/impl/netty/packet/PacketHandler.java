/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
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
package net.tridentsdk.impl.netty.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.tridentsdk.api.Trident;
import net.tridentsdk.impl.packets.login.PacketLoginOutDisconnect;
import net.tridentsdk.impl.packets.play.out.PacketPlayOutDisconnect;
import net.tridentsdk.impl.TridentServer;
import net.tridentsdk.impl.netty.ClientConnection;
import net.tridentsdk.impl.netty.protocol.Protocol;

import javax.annotation.concurrent.ThreadSafe;

/**
 * The channel handler that is placed into the netty connection bootstrap to process inbound messages from clients (not
 * just players)
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class PacketHandler extends SimpleChannelInboundHandler<PacketData> {
    private final Protocol protocol;
    private ClientConnection connection;

    public PacketHandler() {
        this.protocol = ((TridentServer) Trident.getServer()).getProtocol();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext context) {
        this.connection = ClientConnection.getConnection(context);
    }

    /**
     * Converts the PacketData to a Packet depending on the ConnectionStage of the Client <p/> {@inheritDoc}
     */
    @Override
    protected void messageReceived(ChannelHandlerContext context, PacketData data)
            throws Exception {

        if (this.connection.isEncryptionEnabled()) {
            data.decrypt(this.connection);
        }

        Packet packet = this.protocol.getPacket(data.getId(), this.connection.getStage(), PacketType.IN);

        //If packet is unknown disconnect the client, as said client seems to be modified
        if (packet.getId() == -1) {
            this.connection.logout();

            // TODO Print client info. stating that has sent an invalid packet and has been disconnected
            return;
        }

        System.out.println("Received packet: " + packet.getClass().getSimpleName());

        // decode and handle the packet
        packet.decode(data.getData());

        try {
            packet.handleReceived(this.connection);
        } catch (Exception ex) {
            ex.printStackTrace();

            switch (this.connection.getStage()) {
                case LOGIN:
                    PacketLoginOutDisconnect disconnect = new PacketLoginOutDisconnect();

                    disconnect.setJsonMessage(ex.getMessage());

                    this.connection.sendPacket(disconnect);
                    this.connection.logout();

                    // fall through

                case PLAY:
                    PacketPlayOutDisconnect quit = new PacketPlayOutDisconnect();

                    quit.set("reason", ex.getMessage());

                    this.connection.sendPacket(quit);
                    this.connection.logout();

                    // fall through

                default:
                    break;
            }
        }
    }
}
