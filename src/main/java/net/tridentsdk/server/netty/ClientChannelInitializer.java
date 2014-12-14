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
package net.tridentsdk.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.SocketChannel;
import net.tridentsdk.server.netty.packet.*;
import net.tridentsdk.util.TridentLogger;

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
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        super.disconnect(ctx, promise);

        this.connection.logout();
    }
}
