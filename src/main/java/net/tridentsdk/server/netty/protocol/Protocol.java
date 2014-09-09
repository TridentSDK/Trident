/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.server.netty.protocol;

import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketType;

import javax.annotation.concurrent.ThreadSafe;

/**
 * The base implementation for the protocol handling internals
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class Protocol {
    private final Play play = new Play();
    private final Status status = new Status();
    private final Login login = new Login();
    private final Handshake handshake = new Handshake();

    public Play getPlay() {
        return this.play;
    }

    public Status getStatus() {
        return this.status;
    }

    public Login getLogin() {
        return this.login;
    }

    public Handshake getHandshake() {
        return this.handshake;
    }

    public Packet getPacket(int id, ClientStage stage, PacketType type) {
        switch (stage) {
        case PLAY:
            return this.play.getPacket(id, type);

        case HANDSHAKE:
            return this.handshake.getPacket(id, type);

        case STATUS:
            return this.status.getPacket(id, type);

        case LOGIN:
            return this.status.getPacket(id, type);

        default:
            throw new IllegalArgumentException(stage + " is not supported for Protocol#getPacket!");
        }
    }

    public enum ClientStage {
        // TODO Add this to a more appropriate class
        PLAY, STATUS, LOGIN, HANDSHAKE
    }
}
