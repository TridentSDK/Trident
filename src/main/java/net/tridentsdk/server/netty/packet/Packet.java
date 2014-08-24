package net.tridentsdk.server.netty.packet;

import io.netty.buffer.ByteBuf;

public interface Packet {
	
	public Packet decode(ByteBuf buf);

    public ByteBuf encode();
	
	public PacketType getType();
}
