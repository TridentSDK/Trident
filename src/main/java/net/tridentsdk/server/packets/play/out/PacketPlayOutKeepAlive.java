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
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PacketPlayOutKeepAlive extends OutPacket {

    protected int keepAliveId = -1;

    @Override
    public int getId() {
        return 0x00;
    }

    public int getKeepAliveId() {
        return this.keepAliveId;
    }

    @Override
    public void encode(ByteBuf buf) {
        if(keepAliveId == -1) {
            return; // id was not set
        }

        Codec.writeVarInt32(buf, this.keepAliveId);
    }
}
