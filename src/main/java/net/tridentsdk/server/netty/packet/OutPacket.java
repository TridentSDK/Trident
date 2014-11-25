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
        this.fastClass.getField(name).set(this, value);
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
