package net.tridentsdk.server.packets.handshake.client;

import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketType;
import net.tridentsdk.server.netty.protocol.Protocol4;
import io.netty.buffer.ByteBuf;

public class HandshakeClientHandshakePacket implements Packet {
	int protocolVersion;
	String address;
	short port;
	int nextState;
	
	@Override
	public Packet decode(ByteBuf buf) {
		protocolVersion = Codec.readVarInt32(buf);
		address = Codec.readString(buf);
		port = buf.readShort();
		nextState = Codec.readVarInt32(buf);
		return this;
	}

	@Override
	public PacketType getType() {
		return Protocol4.Handshake.Client.HANDSHAKE;
	}

	

}
