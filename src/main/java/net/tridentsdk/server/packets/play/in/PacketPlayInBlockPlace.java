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
package net.tridentsdk.server.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.util.Vector;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketPlayInBlockPlace extends InPacket {

    /**
     * Location of the block being placed
     */
    protected Location location;
    protected byte direction; // wat
    /**
     * Position of the cursor, incorrect use of a Vector xD
     */
    protected Vector cursorPosition;

    @Override
    public int getId() {
        return 0x08;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        long encodedLocation = buf.readLong();

        this.location = new Location(null, (double) (encodedLocation >> 38), (double) (encodedLocation << 26 >> 52),
                (double) (encodedLocation << 38 >> 38));
        this.direction = buf.readByte();

        // ignore held item
        for (int i = 0; i < buf.readableBytes() - 3; i++) {
            buf.readByte();
        }

        double x = (double) buf.readByte();
        double y = (double) buf.readByte();
        double z = (double) buf.readByte();

        this.cursorPosition = new Vector(x, y, z);
        return this;
    }

    public Location getLocation() {
        return this.location;
    }

    public byte getDirection() {
        return this.direction;
    }

    public Vector getCursorPosition() {
        return this.cursorPosition;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly
    }
}
