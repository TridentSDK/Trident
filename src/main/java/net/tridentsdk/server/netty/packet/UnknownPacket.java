package net.tridentsdk.server.netty.packet;

import net.tridentsdk.server.netty.protocol.Protocol4;
import io.netty.buffer.ByteBuf;

/**
 * Used to represent any erroneous packets recieved
 *
 */
public class UnknownPacket extends Packet {

	@Override
	public Packet decode(ByteBuf buf) {
		return this;
	}

	@Override
	public PacketType getType() {
		return Protocol4.Unknown.UNKNOWN;
	}

}
