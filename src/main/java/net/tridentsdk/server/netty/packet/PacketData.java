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
package net.tridentsdk.server.netty.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.util.TridentLogger;

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
            TridentLogger.error(ex);
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
