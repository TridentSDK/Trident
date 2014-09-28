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
import net.tridentsdk.api.reflect.FastClass;
import net.tridentsdk.server.netty.ClientConnection;

/**
 * @author The TridentSDK Team
 */
public abstract class OutPacket implements Packet {

    private final FastClass fastClass;

    public OutPacket() {

        this.fastClass = FastClass.get(this.getClass());
    }

    @Override
    public PacketType getType() {
        return PacketType.OUT;
    }

    /**
     * Sets the field name with said value
     *
     * @param name  Name of field you wish to set
     * @param value Value you wish to set the field to
     * @return OutPacket instance
     */
    public OutPacket set(String name, Object value) {
        this.fastClass.getField(this, name).set(value);
        return this;
    }

    /**
     * {@inheritDoc} <p/> <p>Cannot be decoded</p>
     */
    @Override
    public Packet decode(ByteBuf buf) {
        throw new UnsupportedOperationException(this.getClass().getName() + " cannot be decoded!");
    }

    /**
     * {@inheritDoc} <p/> <p>Cannot be received</p>
     */
    @Override
    public void handleReceived(ClientConnection connection) {
        throw new UnsupportedOperationException(
                this.getClass().getName() + " is a client-bound packet therefor cannot be handled!");
    }
}
