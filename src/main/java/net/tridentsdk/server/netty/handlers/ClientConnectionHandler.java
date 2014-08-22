package net.tridentsdk.server.netty.handlers;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ClientConnectionHandler extends ChannelHandlerAdapter {

    // TODO: Store client object

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        // TODO: Handle data
    }

}
