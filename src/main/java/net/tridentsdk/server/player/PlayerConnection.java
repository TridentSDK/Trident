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

import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.OutPacket;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketHandler;
import net.tridentsdk.server.netty.protocol.Protocol;
import net.tridentsdk.server.packets.play.in.PacketPlayInKeepAlive;
import net.tridentsdk.server.packets.play.out.PacketPlayOutKeepAlive;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents the connection the player has to the server
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class PlayerConnection extends ClientConnection {
    private final TridentPlayer player;

    @GuardedBy("this")
    private int keepAliveId = -1;
    @GuardedBy("this")
    private int readCounter = 0;
    @GuardedBy("this")
    private int writeCounter = 0;

    private PlayerConnection(ClientConnection connection, TridentPlayer player) {
        // remove old connection, and replace it with this one
        ClientConnection.clientData.put(connection.address(), connection);

        super.address = connection.address();
        super.channel = connection.channel();
        super.loginKeyPair = connection.loginKeyPair();
        super.sharedSecret = connection.sharedSecret();
        super.stage = Protocol.ClientStage.PLAY; // stage must be PLAY to actually create PlayerConnection
        super.encryptionEnabled = connection.isEncryptionEnabled();
        super.compressionEnabled = connection.isCompressionEnabled();

        this.player = player;

        // update the clients packet handler
        PacketHandler handler = channel.pipeline().get(PacketHandler.class);

        if (handler != null) { // while unlikely, I'll take my chances
            handler.updateConnection(this);
        }
    }

    public static PlayerConnection createPlayerConnection(ClientConnection connection, TridentPlayer player) {
        return new PlayerConnection(connection, player);
    }

    public static PlayerConnection connection(InetSocketAddress address) {
        return (PlayerConnection) ClientConnection.getConnection(address);
    }

    /**
     * Gets the player that has this connection
     *
     * @return the player that is wrapped
     */
    public TridentPlayer player() {
        return this.player;
    }

    @InternalUseOnly
    public synchronized void sendKeepAlive() {
        int oldId = keepAliveId;

        if (oldId != -1)
            return;

        int id = ThreadLocalRandom.current().nextInt(0x230000);

        OutPacket packet = new PacketPlayOutKeepAlive();

        packet.set("keepAliveId", id);
        keepAliveId = id;

        sendPacket(packet);
    }

    @InternalUseOnly
    public synchronized void handleKeepAlive(PacketPlayInKeepAlive keepAlive) {
        int currentId = keepAliveId;

        if (keepAlive.id() != currentId)
            return;

        keepAliveId = -1;
        readCounter = 0;
    }

    @InternalUseOnly
    public synchronized void resetReadCounter() {
        readCounter = 0;
    }

    @Override
    public void sendPacket(Packet packet) {
        super.sendPacket(packet);

        synchronized (this) {
            writeCounter = 0; // reset write counter
        }
    }

    // Entire method is not needed to be synchronized
    // But release and reacquire from conditions can be expensive
    // Lock striping can be performed by the JIT anyways
    synchronized void tick() {
        ++writeCounter;
        ++readCounter;

        int read = readCounter;
        int write = writeCounter;

        if (read >= 300) {
            if (keepAliveId == -1) {
                sendKeepAlive();
            } else if (read >= 600) {
                player.kickPlayer("Timed out!");
            }
        }

        if (write >= 300) {
            sendKeepAlive();
        }
    }

    /**
     * Checks the hasSent flag to see if the player has sent the keep alive packet
     *
     * @return {@code true} to represent the player keep alive has been sent
     */
    public synchronized boolean hasSentKeepAlive() {
        return keepAliveId == -1;
    }
}
