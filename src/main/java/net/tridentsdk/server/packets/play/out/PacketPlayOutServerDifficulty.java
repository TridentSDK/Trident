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
import net.tridentsdk.server.netty.packet.OutPacket;
import net.tridentsdk.world.settings.Difficulty;

public class PacketPlayOutServerDifficulty extends OutPacket {
    protected Difficulty difficulty;

    @Override
    public int id() {
        return 0x0D;
    }

    public Difficulty difficulty() {
        return this.difficulty;
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeByte((int) this.difficulty.asByte());
    }
}
