package net.tridentsdk.server.netty.protocol;

import net.tridentsdk.server.netty.packet.PacketType;

public interface TridentProtocol {
	
	public PacketType getPacket(int id);
}
