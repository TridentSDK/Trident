package net.tridentsdk.server.netty.packet;

import io.netty.buffer.ByteBuf;

public interface PacketType {
	
	public int id();
	
	public Packet create(ByteBuf buf);
}
