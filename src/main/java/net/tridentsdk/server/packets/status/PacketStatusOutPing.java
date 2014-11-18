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
package net.tridentsdk.server.packets.status;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.packet.OutPacket;
import net.tridentsdk.server.netty.packet.PacketType;

/**
 * The response sent by the server for PacketStatusInPing
 *
 * @author The TridentSDK Team
 * @see PacketStatusInPing
 */
public class PacketStatusOutPing extends OutPacket {
    /**
     * Time of the client
     */
    protected long clientTime;

    @Override
    public int getId() {
        return 0x01;
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeLong(this.clientTime);
    }

    @Override
    public PacketType getType() {
        return PacketType.OUT;
    }
}
