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
package net.tridentsdk.server.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.plugin.channel.ChannelManager;
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
        PluginChannel channel = ChannelManager.getInstance().getPluginChannel(this.channel);

        if (channel != null) {
            channel.process(this.data);
        }
    }
}
