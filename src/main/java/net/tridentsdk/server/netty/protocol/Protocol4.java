package net.tridentsdk.server.netty.protocol;

import net.tridentsdk.server.netty.packet.Packet;
import net.tridentsdk.server.netty.packet.PacketType;
import net.tridentsdk.server.netty.packet.UnknownPacket;
import net.tridentsdk.server.packets.handshake.client.PacketClientHandshake;
import io.netty.buffer.ByteBuf;

public class Protocol4 implements TridentProtocol {
	
	@Override
	public PacketType getPacket(int id) {
		for (PacketType type : Handshake.Client.values()) {
			if (type.id() == id) {
				return type;
			}
		}
		
		return Unknown.UNKNOWN;
	}

    public static class Handshake {

        public static enum Client implements PacketType {
            HANDSHAKE {
                @Override
                public int id() {
                    return 0x00;
                }

                @Override
                public Packet create(ByteBuf buf) {
                    return new PacketClientHandshake().decode(buf);
                }
            }
        }


    }


    public static enum Unknown implements PacketType {
        UNKNOWN {
            @Override
            public int id() {
                return -1;
            }

            @Override
            public Packet create(ByteBuf buf) {
                return new UnknownPacket();
            }
        }
    }
}
