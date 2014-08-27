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
 * Packet information, such as identification and serialized form
 *
 * @author The TridentSDK Team
 */
public class PacketData {
    private final int     id;
    private final ByteBuf data;

    /**
     * Wraps the packet raw information
     *
     * @param id   the packet ID as assigned by the protocol
     * @param data the serialized form of the packet
     */
    public PacketData(int id, ByteBuf data) {
        this.id = id;
        this.data = data;
    }

    /**
     * Gets the packet identification number
     *
     * @return the packet ID
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets the serialized packet
     *
     * @return the serialized packet
     */
    public ByteBuf getData() {
        return this.data;
    }

    /**
     * The amount of bytes that can be read from the serialized packet
     *
     * @return the byte length of the serialized data
     */
    public int getLength() {
        return this.data.readableBytes();
    }
}
