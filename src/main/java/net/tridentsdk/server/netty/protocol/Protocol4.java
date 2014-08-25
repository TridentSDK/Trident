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

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.packet.*;
import net.tridentsdk.server.packets.handshake.client.PacketClientHandshake;

import javax.annotation.concurrent.ThreadSafe;

/**
 * The base implementation for the protocol handling internals
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class Protocol4 implements TridentProtocol {
    @Override
    public PacketType getPacket(int id) {
        for (PacketType type : Protocol4.Handshake.Client.values()) {
            if (type.id() == id) {
                return type;
            }
        }

        return Protocol4.Unknown.UNKNOWN;
    }

    /**
     * The packet type representing {@link net.tridentsdk.server.packets.handshake.client.PacketClientHandshake}
     *
     * @author The TridentSDK Team
     */
    public static class Handshake {
        /**
         * Enumeration overridden to hold the values and factories to the handshaking packet
         *
         * @author The TridentSDK Team
         */
        public enum Client implements PacketType {
            HANDSHAKE {
                @Override
                public int id() {
                    return 0x00; // = 0
                }

                @Override
                public Packet create(ByteBuf buf) {
                    return new PacketClientHandshake().decode(buf);
                }
            }
        }
    }

    /**
     * The packet type representing {@link net.tridentsdk.server.netty.packet.UnknownPacket}. Also overrides the packet
     * type enumeration for value and factory methods.
     *
     * @author The TridentSDK Team
     */
    public enum Unknown implements PacketType {
        UNKNOWN {
            @Override
            public int id() {
                return -1;
            }

            @Override
            public Packet create(ByteBuf buf) {
                return new UnknownPacket();
            }
        }
    }
}
