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
import net.tridentsdk.base.Position;
import net.tridentsdk.server.data.PositionWritable;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PacketPlayOutUpdateBlockEntity extends OutPacket {
    protected Position loc;
    protected int action;
    protected byte[] nbtData;

    @Override
    public int id() {
        return 0x35;
    }

    public Position location() {
        return this.loc;
    }

    public int action() {
        return this.action;
    }

    public byte[] nbtData() {
        return this.nbtData;
    }

    @Override
    public void encode(ByteBuf buf) {
        new PositionWritable(this.loc).write(buf);
        buf.writeByte(this.action);
        buf.writeBytes(this.nbtData);
    }
}
