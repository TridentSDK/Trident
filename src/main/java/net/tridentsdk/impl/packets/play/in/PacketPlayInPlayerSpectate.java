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
package net.tridentsdk.impl.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.impl.netty.ClientConnection;
import net.tridentsdk.impl.netty.packet.InPacket;
import net.tridentsdk.impl.netty.packet.Packet;

import java.util.UUID;

/**
 * Packet sent when player is spectating, need to research more about this packet
 */
public class PacketPlayInPlayerSpectate extends InPacket {

    /**
     * Target player, this might imply that the player is able to set other players to spectate mode? If so, we'll need
     * to fix that
     */
    protected UUID uuid;

    @Override
    public int getId() {
        return 0x18;
    }

    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.uuid = new UUID(buf.readLong(), buf.readLong());

        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly
    }
}
