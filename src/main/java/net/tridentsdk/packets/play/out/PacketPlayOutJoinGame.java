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
package net.tridentsdk.packets.play.out;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.Difficulty;
import net.tridentsdk.api.GameMode;
import net.tridentsdk.api.world.Dimension;
import net.tridentsdk.api.world.LevelType;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PacketPlayOutJoinGame extends OutPacket {

    protected int entityId;
    protected GameMode gamemode;
    protected Dimension dimension;

    protected Difficulty difficulty;
    protected short maxPlayers;
    protected LevelType levelType;

    @Override
    public int getId() {
        return 0x01;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public GameMode getGamemode() {
        return this.gamemode;
    }

    public Dimension getDimension() {
        return this.dimension;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    public short getMaxPlayers() {
        return this.maxPlayers;
    }

    public LevelType getLevelType() {
        return this.levelType;
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeInt(this.entityId);

        buf.writeByte((int) this.gamemode.toByte());
        buf.writeByte((int) this.dimension.toByte());
        buf.writeByte((int) this.difficulty.toByte());
        buf.writeByte((int) this.maxPlayers);

        Codec.writeString(buf, this.levelType.toString());
        buf.writeBoolean(true);
    }
}
