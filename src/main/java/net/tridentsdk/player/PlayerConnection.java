/*
 * Copyright (c) 2014, TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of TridentSDK nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.player;

import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.protocol.Protocol;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents the connection the player has to the server
 *
 * @author The TridentSDK Team
 */
public class PlayerConnection extends ClientConnection {
    private final TridentPlayer player;
    private final AtomicLong keepAliveSent = new AtomicLong(0L);
    private volatile int keepAliveId;

    PlayerConnection(ClientConnection connection, TridentPlayer player) {
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
     * @param id the keep alive number to be set to
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
