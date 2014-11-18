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
package net.tridentsdk.server.netty.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;

/**
 * Wraps the raw Packet Data/Bytes receieved over the network (May serve more functions later)
 *
 * @author The TridentSDK Team
 */
public class PacketData {
    private final ByteBuf rawData;
    private ByteBuf decrypted;
    private Integer id;

    /**
     * Wraps the packet raw information
     *
     * @param data the serialized form of the packet
     */
    public PacketData(ByteBuf data) {
        this.rawData = data;
    }

    /**
     * Gets the Id of the packet. Reads it if it hasn't been read
     *
     * @return id the id of the packet
     */
    public int getId() {
        return this.id == null ? this.id = Codec.readVarInt32(this.getData()) : this.id;
    }

    /**
     * Gets the appropriate packet data
     *
     * @return the serialized packet
     */
    public ByteBuf getData() {
        return this.decrypted != null ? this.decrypted : this.rawData;
    }

    public void decrypt(ClientConnection con) {
        try {
            this.decrypted = Unpooled.buffer();

            this.decrypted.writeBytes(con.encrypt(Codec.toArray(this.rawData)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * The amount of bytes that can be read from the serialized packet
     *
     * @return the byte length of the serialized data
     */
    public int getLength() {
        return this.getData().readableBytes();
    }
}
