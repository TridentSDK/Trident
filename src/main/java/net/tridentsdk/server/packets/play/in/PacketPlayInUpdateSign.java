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
import net.tridentsdk.Location;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

/**
 * Packet is sent when a player wishes to update a sign
 */
public class PacketPlayInUpdateSign extends InPacket {

    /**
     * Contents of the sign, represented in JSON
     */
    protected final String[] jsonContents = new String[4];
    /**
     * Location of the sign
     */
    protected Location signLocation;

    @Override
    public int getId() {
        return 0x12;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        long encoded = buf.readLong();
        double x = (double) (encoded >> 38);
        double y = (double) (encoded << 26 >> 52);
        double z = (double) (encoded << 38 >> 38);

        this.signLocation = new Location(null, x, y, z);

        for (int i = 0; i <= 4; i++) {
            this.jsonContents[i] = Codec.readString(buf);
        }
        return this;
    }

    public Location getSignLocation() {
        return this.signLocation;
    }

    public String[] getJsonContents() {
        return this.jsonContents;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly (reminder: update world)
    }
}
