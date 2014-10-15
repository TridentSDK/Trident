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
package net.tridentsdk.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.SocketChannel;
import net.tridentsdk.server.netty.packet.PacketDecoder;
import net.tridentsdk.server.netty.packet.PacketDecrypter;
import net.tridentsdk.server.netty.packet.PacketEncoder;
import net.tridentsdk.server.netty.packet.PacketEncrypter;
import net.tridentsdk.server.netty.packet.PacketHandler;

import javax.annotation.concurrent.ThreadSafe;

/**
 * The netty channel initializer which appends the handlers to the socket channel
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    private ClientConnection connection;

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        //channel.config().setOption(ChannelOption.IP_TOS, 24);
        channel.config().setOption(ChannelOption.TCP_NODELAY, true);

        this.connection = ClientConnection.registerConnection(channel);

        //Decode:
        channel.pipeline().addLast(new PacketDecrypter());
        channel.pipeline().addLast(new PacketDecoder());
        channel.pipeline().addLast(new PacketHandler());

        //Encode:
        channel.pipeline().addLast(new PacketEncrypter());
        channel.pipeline().addLast(new PacketEncoder());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        this.connection.logout();
        System.out.println("Logged out client!");
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        super.disconnect(ctx, promise);

        this.connection.logout();
        System.out.println("Logged out client!");
    }
}
