package net.tridentsdk.server.netty.protocol;

import net.tridentsdk.server.netty.packet.PacketType;

public abstract class TridentProtocol {
	
	public abstract PacketType getPacket(int id);
}
