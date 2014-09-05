package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketPlayInEntityAction extends InPacket {

    private ActionType type;
    private int        jumpBoost; // because people at Mojang are fucking retards

    @Override
    public int getId() {
        return 0x0B;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        Codec.readVarInt32(buf); // ignore entity id as its the player's
        this.type = ActionType.getAction(buf.readUnsignedByte());
        this.jumpBoost = Codec.readVarInt32(buf);

        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly
    }

    public enum ActionType {
        CROUCH(0),
        UN_CROUCH(1),
        LEAVE_BED(2),
        START_SPRINTING(3),
        STOP_SPRINTING(4),
        ON_HORSE(5),
        OPEN_INVENTORY(6);

        private final int id;

        ActionType(int id) {
            this.id = id;
        }

        public static ActionType getAction(int id) {
            for (ActionType type : values()) {
                if (type.id == id)
                    return type;
            }

            throw new IllegalArgumentException(id + " is not a valid ActionType id!");
        }

        public int getId() {
            return id;
        }
    }
}
