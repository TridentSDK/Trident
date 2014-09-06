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

package net.tridentsdk.server.netty.packet;

import io.netty.buffer.ByteBuf;

/**
 * Packet information, such as identification and serialized form
 *
 * @author The TridentSDK Team
 */
public class PacketData {
    private final int id;
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
