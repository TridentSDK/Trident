/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *     3. Neither the name of TridentSDK nor the names of its
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

package net.tridentsdk.packets.play.out;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PacketPlayOutTeams extends OutPacket {

    private String teamName;
    private short mode; // TODO: change to enum

    private String teamDisplay;
    private String teamPrefix;
    private String teamSuffix;

    private short friendlyFire;
    private String nameTagVisibility; // TODO: change to enum
    private short color;

    private String[] players;

    @Override
    public int getId() {
        return 0x3E;
    }

    public String getTeamName() {
        return teamName;
    }

    public short getMode() {
        return mode;
    }

    public String getTeamDisplay() {
        return teamDisplay;
    }

    public String getTeamPrefix() {
        return teamPrefix;
    }

    public String getTeamSuffix() {
        return teamSuffix;
    }

    public short getFriendlyFire() {
        return friendlyFire;
    }

    public String getNameTagVisibility() {
        return nameTagVisibility;
    }

    public short getColor() {
        return color;
    }

    public String[] getPlayers() {
        return players;
    }

    @Override
    public void encode(ByteBuf buf) {
        Codec.writeString(buf, teamName);
        buf.writeByte(mode);

        if(mode == 1 || mode == 2) {
            Codec.writeString(buf, teamDisplay);
            Codec.writeString(buf, teamPrefix);
            Codec.writeString(buf, teamSuffix);

            buf.writeByte(friendlyFire);
            Codec.writeString(buf, nameTagVisibility);
            buf.writeByte(color);
        }

        if(mode == 3 || mode == 4) {
            Codec.writeVarInt32(buf, players.length);

            for(String s : players) {
                Codec.writeString(buf, s);
            }
        }
    }
}
