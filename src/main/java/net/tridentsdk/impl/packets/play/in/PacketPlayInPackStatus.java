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
import net.tridentsdk.impl.netty.Codec;
import net.tridentsdk.impl.netty.packet.InPacket;
import net.tridentsdk.impl.netty.packet.Packet;

/**
 * Sent by the client to state the status of downloading the resource pack
 */
public class PacketPlayInPackStatus extends InPacket {

    /**
     * Hash of the pack
     */
    protected String hash;
    /**
     * Result/Status <p/> 0 - Successfully loaded 1 - Declined 2 - Failed download 3 - Accepted <p/> TODO Change to
     * enum
     */
    protected int result;

    @Override
    public int getId() {
        return 0x19;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.hash = Codec.readString(buf);
        this.result = Codec.readVarInt32(buf);

        return this;
    }

    public String getHash() {
        return this.hash;
    }

    public int getResult() {
        return this.result;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly
    }
}
