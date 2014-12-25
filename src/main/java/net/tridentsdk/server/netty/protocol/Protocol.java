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

package net.tridentsdk.server.netty.protocol;

import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketType;
import net.tridentsdk.util.TridentLogger;

import javax.annotation.concurrent.ThreadSafe;

/**
 * The base implementation for the protocol handling internals
 *
 * @author The TridentSDK Team
 */
@ThreadSafe public class Protocol {
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
                return this.login.getPacket(id, type);

            default:
                TridentLogger.error(new IllegalArgumentException(stage + " is not supported for Protocol#getPacket!"));
        }
        return null;
    }

    public enum ClientStage {
        // TODO Add this to a more appropriate class
        PLAY, STATUS, LOGIN, HANDSHAKE
    }
}
