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
package net.tridentsdk.impl.netty.packet;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.impl.netty.ClientConnection;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Used to represent any erroneous inPackets received
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class UnknownPacket implements Packet {
    @Override
    public Packet decode(ByteBuf buf) {
        return this;
    }

    /**
     * {@inheritDoc} <p/> <p>Cannot be encoded. Throws UnsupportedOperationException</p>
     */
    @Override
    public void encode(ByteBuf buf) {
        throw new UnsupportedOperationException("Cannot serialize unknown packet");
    }

    @Override
    public int getId() {
        return -1;
    }

    /**
     * {@inheritDoc} <p/> <p>Returns {@code null}, since we don't know where the packet came from</p>
     */
    @Override
    public PacketType getType() {
        return null;
    }

    /**
     * {@inheritDoc} <p/> <p>Does not do anything</p>
     */
    @Override
    public void handleReceived(ClientConnection connection) {
    }
}
