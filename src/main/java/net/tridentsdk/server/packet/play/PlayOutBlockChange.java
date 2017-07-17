package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.base.Position;
import net.tridentsdk.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;

import static net.tridentsdk.server.net.NetData.wvec;
import static net.tridentsdk.server.net.NetData.wvint;

/**
 * Sent by the server to indicate to the client that the
 * block at the given location has changed to the new
 * value.
 */
@Immutable
public final class PlayOutBlockChange extends PacketOut {
    private final Position block;
    private final int newBlock;

    public PlayOutBlockChange(Position block, int newBlock) {
        super(PlayOutBlockChange.class);
        this.block = block;
        this.newBlock = newBlock;
    }

    @Override
    public void write(ByteBuf buf) {
        wvec(buf, this.block);
        wvint(buf, this.newBlock);
    }
}