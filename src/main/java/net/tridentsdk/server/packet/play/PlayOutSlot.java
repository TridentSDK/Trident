package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.net.Slot;
import net.tridentsdk.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;

/**
 * Sets a slot in the given position of the given window
 * to the given slot data.
 */
@Immutable
public class PlayOutSlot extends PacketOut {
    /**
     * The window to set the slot, 0 for player inventory
     */
    private final int window;
    /**
     * The slot to place the item into
     */
    private final int pos;
    /**
     * The slot data
     */
    private final Slot slot;

    public PlayOutSlot(int window, int pos, Slot slot) {
        super(PlayOutSlot.class);
        this.window = window;
        this.pos = pos;
        this.slot = slot;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeByte(this.window);
        buf.writeShort(this.pos);
        this.slot.write(buf);
    }
}