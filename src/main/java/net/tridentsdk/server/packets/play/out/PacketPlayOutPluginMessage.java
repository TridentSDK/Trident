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

public class PacketPlayOutPluginMessage extends OutPacket {

    protected String channel;
    protected byte[] data;

    @Override
    public int getId() {
        return 0x3F;
    }

    public String getChannel() {
        return this.channel;
    }

    public byte[] getData() {
        return this.data;
    }

    @Override
    public void encode(ByteBuf buf) {
        Codec.writeString(buf, this.channel);
        Codec.writeVarInt32(buf, data.length);
        buf.writeBytes(this.data);
    }

    public static final OutPacket VANILLA_CHANNEL = new PacketPlayOutPluginMessage()
            .set("channel","MC|Brand")
            .set("data", new byte[]{'v','a','n','i','l','l','a'});

}
