package net.tridentsdk.server.netty.packet;

import io.netty.buffer.ByteBuf;

public abstract class Packet {
	
	public abstract Packet decode(ByteBuf buf);
	
	public abstract PacketType getType();
}
