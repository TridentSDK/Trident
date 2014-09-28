/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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

        player.setLocale(locale);
    }
}
