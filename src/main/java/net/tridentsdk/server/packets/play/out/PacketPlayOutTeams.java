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

package net.tridentsdk.server.packets.play.out;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.base.board.TagVisibility;
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
    public int id() {
        return 0x41;
    }

    public String team() {
        return this.teamName;
    }

    public Mode mode() {
        return this.mode;
    }

    public String teamDisplay() {
        return this.teamDisplay;
    }

    public String teamPrefix() {
        return this.teamPrefix;
    }

    public String teamSuffix() {
        return this.teamSuffix;
    }

    public short friendlyFire() {
        return this.friendlyFire;
    }

    public TagVisibility tagVisibility() {
        return this.tagVisibility;
    }

    public short color() {
        return this.color;
    }

    public String[] players() {
        return this.players;
    }

    @Override
    public void encode(ByteBuf buf) {
        Codec.writeString(buf, this.teamName);
        buf.writeByte((int) this.mode.asByte());

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

        public byte asByte() {
            return this.b;
        }
    }
}
