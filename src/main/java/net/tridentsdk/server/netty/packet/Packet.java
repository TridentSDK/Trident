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

package net.tridentsdk.server.netty.packet;

import io.netty.buffer.ByteBuf;

/**
 * Data bearing abstraction that represents a piece of information to communicate between server and client
 *
 * @author The TridentSDK Team
 */
public interface Packet {
    /**
     * Sets the fields of the packet from the data serialized into the buffer
     *
     * @param buf the buffer storing the serialized packet data
     * @return the this instance of the packet
     */
    Packet decode(ByteBuf buf);

    /**
     * Serialized the data held by this packet to a ByteBuf
     *
     * @return the serialized form of the packet
     */
    ByteBuf encode();

    int getId();
}
