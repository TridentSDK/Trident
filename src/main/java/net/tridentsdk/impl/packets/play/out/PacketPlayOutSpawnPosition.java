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
package net.tridentsdk.impl.packets.play.out;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.Location;
import net.tridentsdk.impl.netty.packet.OutPacket;

public class PacketPlayOutSpawnPosition extends OutPacket {

    protected Location location;

    @Override
    public int getId() {
        return 0x05;
    }

    public Location getLocation() {
        return this.location;
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeLong((long) (((int) this.location.getX() & 0x3FFFFFF) << 6 |
                ((int) this.location.getY() & 0xFFF) << 26 |
                (int) this.location.getZ() & 0x3FFFFFF));
    }
}
