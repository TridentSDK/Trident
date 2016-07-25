/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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
package net.tridentsdk.server.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * The default NetServer implementation that uses NIO
 * multi-threaded server socket handlers
 */
public class NetNioServer extends NetServer {
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(8);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup(8);

    public NetNioServer(String ip, int port) {
        super(ip, port);
    }

    @Override
    public void setup() throws InterruptedException {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new NetChannelInit())
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture future = b.bind(ip(), port()).sync();

        future.channel().closeFuture().sync();
    }

    @Override
    public void shutdown() throws InterruptedException {
        bossGroup.shutdownGracefully().await();
        workerGroup.shutdownGracefully().await();
    }
}