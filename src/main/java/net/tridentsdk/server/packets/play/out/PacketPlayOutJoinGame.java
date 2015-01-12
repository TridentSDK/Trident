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
import net.tridentsdk.Difficulty;
import net.tridentsdk.GameMode;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;
import net.tridentsdk.world.Dimension;
import net.tridentsdk.world.LevelType;

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

        buf.writeByte((int) this.gamemode.asByte());
        buf.writeByte((int) this.dimension.asByte());
        buf.writeByte((int) this.difficulty.asByte());
        buf.writeByte((int) this.maxPlayers);

        Codec.writeString(buf, this.levelType.toString());
        buf.writeBoolean(true);
    }
}
