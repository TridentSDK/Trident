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

package net.tridentsdk.server.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.plugin.channel.ChannelHandler;
import net.tridentsdk.plugin.channel.PluginChannel;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

/**
 * Mods and plugins can use this to send their data.
 */
public class PacketPlayInPluginMessage extends InPacket {

    /**
     * Name of the channel
     */
    protected String channel;
    /**
     * Data it wishes to send
     */
    protected byte[] data;

    @Override
    public int getId() {
        return 0x17;
    }

    public String getChannel() {
        return this.channel;
    }

    public byte[] getData() {
        return this.data;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        this.channel = Codec.readString(buf);
        this.data = new byte[buf.readableBytes()];

        buf.readBytes(this.data);

        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        PluginChannel channel = ChannelHandler.getInstance().forChannel(this.channel);

        if (channel != null) {
            channel.process(this.data);
        }
    }
}
