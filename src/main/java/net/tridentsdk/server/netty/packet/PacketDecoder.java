package net.tridentsdk.server.netty.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

import net.tridentsdk.api.Trident;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.PacketDecoder.State;
import net.tridentsdk.server.netty.protocol.Protocol4;
import net.tridentsdk.server.netty.protocol.TridentProtocol;

public class PacketDecoder extends ReplayingDecoder<State> {
	TridentProtocol protocol;
	private int length;
	
	public PacketDecoder() {
	     super(State.LENGTH);
	     protocol = ((TridentServer) Trident.getServer()).getProtocol();
	  }
	
    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf buf, List<Object> objects) throws Exception {
    	switch (state()) {
            case LENGTH:
                length = Codec.readVarInt32(buf);
                checkpoint(State.DATA);

            case DATA:
                //Makes sure that there are enough bytes for the whole packet
                buf.markReaderIndex();
                buf.readBytes(length);
                buf.resetReaderIndex();

                //Gets the packet type, and reads all data from buffer to the packet
                int id = Codec.readVarInt32(buf);
                Packet packet = protocol.getPacket(id).create(buf);
                packet.decode(buf);

                //If packet is unknown, skip the bytes corresponding to the length
                if (packet.getType().equals(Protocol4.Unknown.UNKNOWN)) {
                    buf.skipBytes(length);
                }

        }
    }
    
    enum State {
    	LENGTH, DATA;
    }

}
