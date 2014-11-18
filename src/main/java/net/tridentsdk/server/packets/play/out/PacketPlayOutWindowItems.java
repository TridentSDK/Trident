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
package net.tridentsdk.server.packets.play.out;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.data.Slot;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PacketPlayOutWindowItems extends OutPacket {

    protected int windowId;
    protected Slot[] slots;

    @Override
    public int getId() {
        return 0x30;
    }

    public int getWindowId() {
        return this.windowId;
    }

    public Slot[] getSlots() {
        return this.slots;
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeByte(this.windowId);
        buf.writeShort(this.slots.length);

        for (Slot s : this.slots) {
            s.write(buf);
        }
    }
}
