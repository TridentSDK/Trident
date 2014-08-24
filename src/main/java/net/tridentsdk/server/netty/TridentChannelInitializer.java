package net.tridentsdk.server.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import net.tridentsdk.server.netty.packet.PacketDecoder;
import net.tridentsdk.server.netty.handlers.ClientConnectionHandler;

public class TridentChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        //channel.config().setOption(ChannelOption.IP_TOS, 24);
        channel.config().setOption(ChannelOption.TCP_NODELAY, true);

        channel.pipeline().addLast(new PacketDecoder()); 
        channel.pipeline().addLast(new ClientConnectionHandler());
    }

}
