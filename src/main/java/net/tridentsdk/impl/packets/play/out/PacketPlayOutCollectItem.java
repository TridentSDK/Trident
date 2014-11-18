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

public class PacketPlayOutCollectItem extends OutPacket {

    protected int collectedId;
    protected int collectorId;

    @Override
    public int getId() {
        return 0x0E;
    }

    public int getCollectedId() {
        return this.collectedId;
    }

    public int getCollectorId() {
        return this.collectorId;
    }

    @Override
    public void encode(ByteBuf buf) {
        Codec.writeVarInt32(buf, this.collectedId);
        Codec.writeVarInt32(buf, this.collectorId);
    }
}
