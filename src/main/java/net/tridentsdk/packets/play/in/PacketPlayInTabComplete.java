/*
 * Copyright (c) 2014, TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of TridentSDK nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.event.player.PlayerTabCompleteEvent;
import net.tridentsdk.packets.play.out.PacketPlayOutTabComplete;
import net.tridentsdk.player.PlayerConnection;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

/**
 * Sent when the user presses tab while writing text. The payload contains all text behind the cursor.
 */
public class PacketPlayInTabComplete extends InPacket {

    /**
     * Text currently written
     */
    protected String text;
    /**
     * If player is looking at a specific block
     */
    protected boolean hasPosition;
    /**
     * Position of the block the player is looking at, only sent if hasPosition is true
     */
    protected Location lookedAtBlock;

    @Override
    public int getId() {
        return 0x14;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.text = Codec.readString(buf);
        this.hasPosition = buf.readBoolean();

        if (this.hasPosition) {
            long encoded = buf.readLong();
            double x = (double) (encoded << 38);
            double y = (double) (encoded << 26 >> 52);
            double z = (double) (encoded << 38 >> 38);

            this.lookedAtBlock = new Location(null, x, y, z);
        }

        return this;
    }

    public String getText() {
        return this.text;
    }

    public boolean isHasPosition() {
        return this.hasPosition;
    }

    public Location getLookedAtBlock() {
        return this.lookedAtBlock;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        PlayerTabCompleteEvent event = new PlayerTabCompleteEvent(
                ((PlayerConnection) connection).getPlayer(), this.text);

        if (event.getSuggestions().length > 0) {
            connection.sendPacket(new PacketPlayOutTabComplete().set("matches", event.getSuggestions()));
        }
    }
}
