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
