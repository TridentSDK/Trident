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
import net.tridentsdk.api.inventory.InventoryType;
import net.tridentsdk.impl.netty.Codec;
import net.tridentsdk.impl.netty.packet.OutPacket;

public class PacketPlayOutOpenWindow extends OutPacket {

    protected int windowId;
    protected InventoryType inventoryType;
    protected String windowTitle;
    protected int slots;
    protected int entityId; // only for horses, since people at Mojang are retards

    @Override
    public int getId() {
        return 0x2D;
    }

    public int getWindowId() {
        return this.windowId;
    }

    public InventoryType getInventoryType() {
        return this.inventoryType;
    }

    public String getWindowTitle() {
        return this.windowTitle;
    }

    public int getSlots() {
        return this.slots;
    }

    public int getEntityId() {
        return this.entityId;
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeByte(this.windowId);

        Codec.writeString(buf, this.inventoryType.toString());
        Codec.writeString(buf, this.windowTitle);

        buf.writeByte(this.slots);
        buf.writeInt(this.entityId); // rip in varints
    }
}
