/*
 * Copyright (C) 2014 The TridentSDK Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tridentsdk.server.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;

import javax.annotation.concurrent.ThreadSafe;

import net.tridentsdk.server.netty.client.ClientConnectionHandler;
import net.tridentsdk.server.netty.packet.PacketDecoder;

/**
 * The netty channel initializer which appends the handlers to the socket channel
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class TridentChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        //channel.config().setOption(ChannelOption.IP_TOS, 24);
        channel.config().setOption(ChannelOption.TCP_NODELAY, true);

        channel.pipeline().addLast(new PacketDecoder());
        channel.pipeline().addLast(new ClientConnectionHandler());
    }
}
