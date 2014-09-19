/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *     3. Neither the name of TridentSDK nor the names of its
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

import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.protocol.Protocol;

import java.util.concurrent.atomic.AtomicReference;

public class PlayerConnection extends ClientConnection {

    private volatile int keepAliveId;
    private volatile long keepAliveSent; // in ticks and relative to player

    private final TridentPlayer player;

    PlayerConnection(ClientConnection connection, TridentPlayer player) {
        clientData.remove(connection.getAddress());
        clientData.put(connection.getAddress(), new AtomicReference<ClientConnection>(this));

        super.address = connection.getAddress();
        super.channel = connection.getChannel();
        super.loginKeyPair = connection.getLoginKeyPair();
        super.sharedSecret = connection.getSharedSecret();
        super.stage = Protocol.ClientStage.PLAY; // stage must be PLAY to actually create PlayerConnection
        super.encryptionEnabled = connection.isEncryptionEnabled();

        this.player = player;
        this.keepAliveId = -1;
    }

    public TridentPlayer getPlayer() {
        return player;
    }

    public int getKeepAliveId() {
        return keepAliveId;
    }

    public void setKeepAliveId(int id, long ticksLived) {
        this.keepAliveId = id;
        this.keepAliveSent = ticksLived;
    }

    /*
     * @NotJavaDoc
     * Relative to player
     */
    public long getKeepAliveSent() {
        return keepAliveSent;
    }
}
