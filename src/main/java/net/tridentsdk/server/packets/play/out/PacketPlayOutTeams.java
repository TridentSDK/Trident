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
package net.tridentsdk.server.packets.play.out;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.board.TagVisibility;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PacketPlayOutTeams extends OutPacket {

    protected String teamName;
    protected Mode mode;

    protected String teamDisplay;
    protected String teamPrefix;
    protected String teamSuffix;

    protected short friendlyFire;
    protected TagVisibility tagVisibility;
    protected short color;

    protected String[] players;

    @Override
    public int getId() {
        return 0x3E;
    }

    public String getTeamName() {
        return this.teamName;
    }

    public Mode getMode() {
        return this.mode;
    }

    public String getTeamDisplay() {
        return this.teamDisplay;
    }

    public String getTeamPrefix() {
        return this.teamPrefix;
    }

    public String getTeamSuffix() {
        return this.teamSuffix;
    }

    public short getFriendlyFire() {
        return this.friendlyFire;
    }

    public TagVisibility getTagVisibility() {
        return this.tagVisibility;
    }

    public short getColor() {
        return this.color;
    }

    public String[] getPlayers() {
        return this.players;
    }

    @Override
    public void encode(ByteBuf buf) {
        Codec.writeString(buf, this.teamName);
        buf.writeByte((int) this.mode.toByte());

        if (this.mode.b == 1 || this.mode.b == 2) {
            Codec.writeString(buf, this.teamDisplay);
            Codec.writeString(buf, this.teamPrefix);
            Codec.writeString(buf, this.teamSuffix);

            buf.writeByte((int) this.friendlyFire);
            Codec.writeString(buf, this.tagVisibility.toString());
            buf.writeByte((int) this.color);
        }

        if (this.mode.b == 3 || this.mode.b == 4) {
            Codec.writeVarInt32(buf, this.players.length);

            for (String s : this.players) {
                Codec.writeString(buf, s);
            }
        }
    }

    public enum Mode {

        CREATED(0),
        REMOVED(1),
        UPDATED(2),
        ADD_PLAYER(3),
        REMOVE_PLAYER(4);

        protected final byte b;

        Mode(int i) {
            this.b = (byte) i;
        }

        public byte toByte() {
            return this.b;
        }
    }
}
