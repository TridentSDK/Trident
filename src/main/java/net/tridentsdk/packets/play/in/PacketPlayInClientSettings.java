/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
