/*
 * Copyright (C) 2014 The TridentSDK Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

import java.util.Locale;

public class PacketPlayInClientSettings extends InPacket {

    private Locale  locale;
    private short   viewDistance;
    private byte    chatFlags;
    private boolean chatColors;

    @Override
    public int getId() {
        return 0x15;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.locale = Locale.forLanguageTag(Codec.readString(buf));
        this.viewDistance = (short) buf.readByte();
        this.chatFlags = buf.readByte();
        this.chatColors = buf.readBoolean();

        buf.readUnsignedByte(); // -shrugs-

        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly
    }
}
