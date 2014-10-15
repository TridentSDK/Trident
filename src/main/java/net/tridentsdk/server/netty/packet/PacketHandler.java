/*
 *     Trident - A Multithreaded Server Alternative
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.server.netty.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.tridentsdk.api.Trident;
import net.tridentsdk.packets.login.PacketLoginOutDisconnect;
import net.tridentsdk.packets.play.out.PacketPlayOutDisconnect;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.protocol.Protocol;

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
            switch (this.connection.getStage()) {
                case LOGIN:
                    PacketLoginOutDisconnect disconnect = new PacketLoginOutDisconnect();

                    disconnect.setJsonMessage(ex.getMessage());

                    this.connection.sendPacket(disconnect);
                    this.connection.logout();

                    break;

                case PLAY:
                    PacketPlayOutDisconnect quit = new PacketPlayOutDisconnect();

                    quit.set("reason", ex.getMessage());

                    this.connection.sendPacket(quit);
                    this.connection.logout();

                    break;

                default:
                    // can't do much but print the stacktrace
                    ex.printStackTrace();
                    break;
            }
        }
    }
}
