/*
 * Copyright (C) 2014 The TridentSDK Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
    private final Play      play      = new Play();
    private final Status    status    = new Status();
    private final Login     login     = new Login();
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
