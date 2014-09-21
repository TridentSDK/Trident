/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
        
        connection = ClientConnection.registerConnection(channel);
        
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

        connection.logout();
        System.out.println("Logged out client!");
    }
    
    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        super.disconnect(ctx, promise);

        connection.logout();
        System.out.println("Logged out client!");
    }
}
