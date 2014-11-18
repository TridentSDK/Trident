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
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PacketPlayOutPlayerRespawn extends OutPacket {

    protected int dimension;
    protected int difficulty;
    protected int gameMode;
    protected String levelType;

    @Override
    public int getId() {
        return 0x07;
    }

    public int getDimension() {
        return this.dimension;
    }

    public int getDifficulty() {
        return this.difficulty;
    }

    public int getGameMode() {
        return this.gameMode;
    }

    public String getLevelType() {
        return this.levelType;
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeInt(this.dimension);
        buf.writeByte(this.difficulty);
        buf.writeByte(this.gameMode);
        Codec.writeString(buf, this.levelType);
    }
}
