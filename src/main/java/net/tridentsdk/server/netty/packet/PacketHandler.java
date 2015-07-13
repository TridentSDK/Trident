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

package net.tridentsdk.server.netty.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.tridentsdk.Trident;
import net.tridentsdk.meta.MessageBuilder;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.protocol.Protocol;
import net.tridentsdk.server.packets.login.PacketLoginOutDisconnect;
import net.tridentsdk.server.packets.play.out.PacketPlayOutDisconnect;
import net.tridentsdk.server.player.PlayerConnection;
import net.tridentsdk.util.TridentLogger;

import javax.annotation.concurrent.ThreadSafe;

/**
 * The channel handler that is placed into the netty connection bootstrap to process inbound messages from clients (not
 * just onlinePlayers)
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class PacketHandler extends SimpleChannelInboundHandler<PacketData> {
    private final Protocol protocol;
    private ClientConnection connection;

    public PacketHandler() {
        this.protocol = ((TridentServer) Trident.instance()).protocol();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext context) {
        this.connection = ClientConnection.connection(context);
    }

    /**
     * Converts the PacketData to a Packet depending on the ConnectionStage of the Client  {@inheritDoc}
     */
    @Override
    protected void messageReceived(ChannelHandlerContext context, PacketData data) throws Exception {

        if (this.connection.isEncryptionEnabled()) {
            data.decrypt(this.connection);
        }

        Packet packet = this.protocol.getPacket(data.getId(), this.connection.stage(), PacketDirection.IN);

        //If packet is unknown disconnect the client, as said client seems to be modified
        if (packet.id() == -1) {
            this.connection.logout();

            if(connection instanceof PlayerConnection) {
                PlayerConnection con = (PlayerConnection) connection;

                TridentLogger.log(con.player().displayName() + " has been disconnected from the server " +
                        "for sending an invalid packet (" +
                        con.address().getHostString() + "," + con.player().uniqueId().toString() + ")");
            }
            return;
        }

        // decode and handle the packet
        packet.decode(data.getData());

        try {
            // DEBUG =====
            // if(!(packet instanceof PacketPlayInPlayerFall) && !(packet instanceof PacketPlayInPlayerMove))
            //    TridentLogger.log("Received packet " + packet.getClass().getSimpleName());
            // =====

            //TODO: add plugin registration for packet handling

            packet.handleReceived(this.connection);

            if (connection instanceof PlayerConnection) {
                ((PlayerConnection) connection).resetReadCounter();
            }
        } catch (Exception ex) {
            TridentLogger.error(ex);

            switch (this.connection.stage()) {
                case LOGIN:
                    PacketLoginOutDisconnect disconnect = new PacketLoginOutDisconnect();

                    disconnect.setJsonMessage(ex.getMessage());

                    this.connection.sendPacket(disconnect);
                    this.connection.logout();

                    // fall through

                case PLAY:
                    PacketPlayOutDisconnect quit = new PacketPlayOutDisconnect();

                    quit.set("reason", new MessageBuilder("\"Internal Error: " + ex.getClass().getName() +
                            ((ex.getMessage() != null) ? ": " + ex.getMessage() : "") + "\"").build().asJson());

                    this.connection.sendPacket(quit);
                    this.connection.logout();

                    // fall through

                default:
                    break;
            }
        }
    }

    public void updateConnection(PlayerConnection connection) {
        this.connection = connection;
    }
}
