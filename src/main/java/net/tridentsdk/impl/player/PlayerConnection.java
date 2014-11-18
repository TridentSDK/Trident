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
package net.tridentsdk.impl.player;

import net.tridentsdk.api.entity.living.Player;
import net.tridentsdk.impl.netty.ClientConnection;
import net.tridentsdk.impl.netty.protocol.Protocol;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents the connection the player has to the impl
 *
 * @author The TridentSDK Team
 */
public class PlayerConnection extends ClientConnection {
    private static final Map<Player, PlayerConnection> PLAYER_MAP = new ConcurrentHashMap<>();

    private final TridentPlayer player;
    private final AtomicLong keepAliveSent = new AtomicLong(0L);
    private volatile int keepAliveId;

    private PlayerConnection(ClientConnection connection, TridentPlayer player) {
        // remove old connection, and replace it with this one
        ClientConnection.clientData.remove(connection.getAddress());
        ClientConnection.clientData.put(connection.getAddress(), new AtomicReference<ClientConnection>(this));

        super.address = connection.getAddress();
        super.channel = connection.getChannel();
        super.loginKeyPair = connection.getLoginKeyPair();
        super.sharedSecret = connection.getSharedSecret();
        super.stage = Protocol.ClientStage.PLAY; // stage must be PLAY to actually create PlayerConnection
        super.encryptionEnabled = connection.isEncryptionEnabled();

        this.player = player;
        this.keepAliveId = -1;
    }

    public static PlayerConnection createPlayerConnection(ClientConnection connection, TridentPlayer player) {
        PlayerConnection conn = new PlayerConnection(connection, player);
        PLAYER_MAP.put(player, conn);
        return conn;
    }

    public static PlayerConnection getConnection(Player player) {
        return PLAYER_MAP.get(player);
    }

    public static PlayerConnection getConnection(InetSocketAddress adress) {
        return (PlayerConnection) ClientConnection.getConnection(adress);
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
     * Gets the ID number of the keep alive value
     *
     * @return the ID for the keep alive packet
     */
    public int getKeepAliveId() {
        return this.keepAliveId;
    }

    /**
     * Sets the keep alive ID number
     *
     * @param id         the keep alive number to be set to
     * @param ticksLived the amount of ticks lived
     */
    public void setKeepAliveId(int id, long ticksLived) {
        this.keepAliveId = id;
        this.keepAliveSent.set(ticksLived);
    }

    /*
     * @NotJavaDoc
     * Relative to player
     */
    public long getKeepAliveSent() {
        return this.keepAliveSent.get();
    }
}
