package net.tridentsdk.packets.play.in;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.client.ClientConnection;
import net.tridentsdk.server.netty.packet.InPacket;
import net.tridentsdk.server.netty.packet.Packet;

public class PacketPlayInPlayerClickWindow extends InPacket {

    private int windowId;
    private int clickedButton;

    private short clickedSlot;
    private short actionNumber;
    private short mode;

    @Override
    public int getId() {
        return 0x0E;
    }

    public int getWindowId() {
        return windowId;
    }

    public int getClickedButton() {
        return clickedButton;
    }

    public short getClickedSlot() {
        return clickedSlot;
    }

    public short getActionNumber() {
        return actionNumber;
    }

    public short getMode() {
        return mode;
    }

    @Override
    public Packet decode(ByteBuf buf) {
        windowId = buf.readByte();
        clickedSlot = buf.readShort();
        clickedButton = buf.readByte();

        actionNumber = buf.readShort();
        mode = buf.readShort();
        return this;
    }

    @Override
    public void handleReceived(ClientConnection connection) {
        // TODO: Act accordingly
    }
}
