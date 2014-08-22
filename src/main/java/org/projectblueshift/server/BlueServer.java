package org.projectblueshift.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.projectblueshift.api.Server;

import java.net.InetSocketAddress;

public class BlueServer implements Server {

    private InetSocketAddress address;

    private final NioEventLoopGroup loopGroup = new NioEventLoopGroup(0,
            (new ThreadFactoryBuilder()).setNameFormat("Netty IO #%d").setDaemon(true).build());
    private ServerBootstrap bootstrap;
    private ChannelFuture channelFuture;

    public BlueServer(InetSocketAddress address) {
        this.address = address;
    }

    public InetSocketAddress getSocketAddress() {
        return address;
    }

    public short getPort() {
        return (short) address.getPort();
    }

    public void init() {
        try{
            bootstrap = new ServerBootstrap();

            channelFuture = bootstrap.channel(NioServerSocketChannel.class).group(loopGroup)
                    .localAddress(address.getAddress(), address.getPort())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true).bind()
                    .syncUninterruptibly();
        }finally{

        }
    }

    public void shutdown() {

    }

}
