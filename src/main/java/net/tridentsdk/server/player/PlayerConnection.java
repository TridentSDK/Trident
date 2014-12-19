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
package net.tridentsdk.server.player;

import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.PacketHandler;
import net.tridentsdk.server.netty.protocol.Protocol;

import javax.annotation.concurrent.ThreadSafe;
import java.net.InetSocketAddress;
import java.util.concurrent.Callable;

/**
 * Represents the connection the player has to the server
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class PlayerConnection extends ClientConnection {
    private final TridentPlayer player;

    private volatile boolean sentKeepAlive = false;

    private PlayerConnection(ClientConnection connection, TridentPlayer player) {
        // remove old connection, and replace it with this one
        ClientConnection.clientData.remove(connection.getAddress());
        ClientConnection.clientData.retrieve(connection.getAddress(), new Callable<ClientConnection>() {
            @Override
            public ClientConnection call() throws Exception {
                return PlayerConnection.this;
            }
        });

        super.address = connection.getAddress();
        super.channel = connection.getChannel();
        super.loginKeyPair = connection.getLoginKeyPair();
        super.sharedSecret = connection.getSharedSecret();
        super.stage = Protocol.ClientStage.PLAY; // stage must be PLAY to actually create PlayerConnection
        super.encryptionEnabled = connection.isEncryptionEnabled();
        super.compressionEnabled = connection.isCompressionEnabled();

        this.player = player;

        // update the clients packet handler
        PacketHandler handler = channel.pipeline().get(PacketHandler.class);

        if(handler != null) { // while unlikely, I'll take my chances
            handler.updateConnection(this);
        }
    }

    public static PlayerConnection createPlayerConnection(ClientConnection connection, TridentPlayer player) {
        return new PlayerConnection(connection, player);
    }

    public static PlayerConnection getConnection(InetSocketAddress address) {
        return (PlayerConnection) ClientConnection.getConnection(address);
    }

    /**
     * Gets the player that has this connection
     *
     * @return the player that is wrapped
     */
    public TridentPlayer getPlayer() {
        return this.player;
    }

    /**
     * Marks the player as kept alive so additional packets do not need to be sent for the current
     * session
     *
     * @param hasSent {@code true} to represent that the packet has been sent
     */
    public void markSentKeepAlive(boolean hasSent) {
        this.sentKeepAlive = true;
    }

    /**
     * Checks the hasSent flag to see if the player has sent the keep alive packet
     *
     * @return {@code true} to represent the player keep alive has been sent
     */
    public boolean hasSentKeepAlive() {
        return this.sentKeepAlive;
    }
}
