package net.tridentsdk.server.netty.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class PacketDecoder extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf byteBuf, List<Object> objects) throws Exception {
        // TODO: Decode bytes
    }

}
