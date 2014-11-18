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
package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.data.Slot;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

/**
 * While the user is in the standard inventory (i.e., not a crafting bench) on a creative-mode server, then this packet
 * will be sent: <p/> If an item is dropped into the quick bar If an item is picked up from the quick bar (item id is
 * -1)
 */
public class PacketPlayInPlayerCreativeAction extends InPacket {

    /**
     * Slot of the action
     */
    protected short slot;
    /**
     * Item used in the action
     */
    protected Slot item;

    @Override
    public int getId() {
        return 0x10;
    }

    public Slot getItem() {
        return this.item;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.slot = buf.readShort();
        this.item = new Slot(buf);

        return this;
    }

    public short getSlot() {
        return this.slot;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly
    }
}
