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
import net.tridentsdk.reflect.FastClass;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.util.TridentLogger;

/**
 * @author The TridentSDK Team
 */
public abstract class OutPacket implements Packet {
    private final FastClass fastClass;

    public OutPacket() {
        this.fastClass = FastClass.get(this.getClass());
    }

    @Override
    public PacketDirection direction() {
        return PacketDirection.OUT;
    }

    /**
     * Sets the field name with said value
     *
     * @param name  Name of field you wish to set
     * @param value Value you wish to set the field to
     * @return OutPacket instance
     */
    public OutPacket set(String name, Object value) {
        this.fastClass.fieldBy(name).set(this, value);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Cannot be decoded</p>
     */
    @Override
    public Packet decode(ByteBuf buf) {
        TridentLogger.error(new UnsupportedOperationException(this.getClass().getName() + " cannot be decoded!"));
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Cannot be received</p>
     */
    @Override
    public void handleReceived(ClientConnection connection) {
        TridentLogger.error(new UnsupportedOperationException(
                this.getClass().getName() + " is a client-bound packet therefor cannot be handled!"));
    }
}
