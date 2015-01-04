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
import net.tridentsdk.util.TridentLogger;

import javax.annotation.concurrent.ThreadSafe;
import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents the connection the player has to the server
 *
 * @author The TridentSDK Team
 */
@ThreadSafe public class PlayerConnection extends ClientConnection {
    private final TridentPlayer player;

    private final AtomicInteger keepAliveId = new AtomicInteger(-1);
    private final AtomicInteger readCounter = new AtomicInteger();
    private final AtomicInteger writeCounter = new AtomicInteger();

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

        if (handler != null) { // while unlikely, I'll take my chances
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

    @InternalUseOnly
    public void sendKeepAlive() {
        int oldId = keepAliveId.get();

        if(oldId != -1)
            return;

        int id = ThreadLocalRandom.current().nextInt();

        OutPacket packet = new PacketPlayOutKeepAlive();

        packet.set("keepAliveId", id);
        keepAliveId.set(id);

        sendPacket(packet);
        TridentLogger.log("id: " + id);
    }

    @InternalUseOnly
    public void handleKeepAlive(PacketPlayInKeepAlive keepAlive) {
        int currentId = keepAliveId.get();

        if(keepAlive.getId() != currentId)
            return;

        keepAliveId.set(-1);
        readCounter.set(0);
    }

    @InternalUseOnly
    public void resetReadCounter() {
        readCounter.set(0);
    }

    @Override
    public void sendPacket(Packet packet) {
        super.sendPacket(packet);

        writeCounter.set(0); // reset write counter
    }

    void tick() {
        writeCounter.incrementAndGet();
        readCounter.incrementAndGet();

        int read = readCounter.get();
        int write = writeCounter.get();

        if(read >= 300) {
            if(keepAliveId.get() == -1) {
                sendKeepAlive();
            } else if(read >= 600) {
                player.kickPlayer("Timed out!");
            }
        }

        if(write >= 300) {
            sendKeepAlive();
        }
    }

    /**
     * Checks the hasSent flag to see if the player has sent the keep alive packet
     *
     * @return {@code true} to represent the player keep alive has been sent
     */
    public boolean hasSentKeepAlive() {
        return keepAliveId.get() == -1;
    }
}
