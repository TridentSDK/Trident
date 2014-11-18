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
package net.tridentsdk.impl.packets.play.out;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.impl.netty.Codec;
import net.tridentsdk.impl.netty.packet.OutPacket;

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
