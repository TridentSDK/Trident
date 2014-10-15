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
import net.tridentsdk.player.PlayerConnection;
import net.tridentsdk.player.TridentPlayer;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

import java.util.Locale;

public class PacketPlayInClientSettings extends InPacket {
    /**
     * Locale of the client, reference to Locale#forLanguageTag(String) to read said locale
     *
     * @see java.util.Locale#forLanguageTag(String)
     */
    protected Locale locale;
    /**
     * View distance set by the client
     */
    protected short viewDistance;
    /**
     * Chat settings: <p/> Bits 0-1. 00: Enabled. 01: Commands only. 10: Hidden.
     */
    protected byte chatFlags;
    /**
     * If the client has colours enabled for chat
     */
    protected boolean chatColors;
    /**
     * Displayed skin parts also packs several values into one byte. <p/> Bit 0: Cape enabled Bit 1: Jacket enabled Bit
     * 2: Left Sleeve enabled Bit 3: Right Sleeve enabled Bit 4: Left Pants Leg enabled Bit 5: Right Pants Leg enabled
     * Bit 6: Hat enabled The most significant bit (bit 7) appears to be unused.
     */
    protected byte skinParts;

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

        this.skinParts = (byte) buf.readUnsignedByte();

        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        TridentPlayer player = ((PlayerConnection) connection).getPlayer();

        player.setLocale(this.locale);
    }
}
