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
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

/**
 * This packet is used to indicate whether the player is on ground (walking/swimming), or airborne (jumping/falling).
 */
public class PacketPlayInPlayerFall extends InPacket {
    /**
     * True if the client is on the ground, False otherwise
     */
    protected boolean onGround;

    @Override
    public int getId() {
        return 0x03;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.onGround = buf.readBoolean();

        return this;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
    }
}
