package org.projectblueshift.server.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import org.projectblueshift.server.netty.handlers.ClientConnectionHandler;
import org.projectblueshift.server.netty.packet.PacketDecoder;

public class BlueChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        channel.config().setOption(ChannelOption.IP_TOS, 24);
        channel.config().setOption(ChannelOption.TCP_NODELAY, false);

        channel.pipeline().addLast(new PacketDecoder(), new ClientConnectionHandler());
    }
}
