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
