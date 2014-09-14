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

package net.tridentsdk.packets.play.out;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.Difficulty;
import net.tridentsdk.api.Gamemode;
import net.tridentsdk.api.world.Dimension;
import net.tridentsdk.api.world.LevelType;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PacketPlayOutJoinGame extends OutPacket {

    private int entityId;
    private Gamemode gamemode;
    private Dimension dimension;

    private Difficulty difficulity;
    private short maxPlayers;
    private LevelType levelType;

    @Override
    public int getId() {
        return 0x01;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public Gamemode getGamemode() {
        return this.gamemode;
    }

    public Dimension getDimension() {
        return this.dimension;
    }

    public Difficulty getDifficulity() {
        return this.difficulity;
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

        buf.writeByte(gamemode.toByte());
        buf.writeByte(dimension.toByte());
        buf.writeByte(difficulity.toByte());
        buf.writeByte((int) this.maxPlayers);

        Codec.writeString(buf, levelType.toString());
        buf.writeBoolean(true);
    }
}
