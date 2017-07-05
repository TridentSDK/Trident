package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;

/**
 * Sent to players to have the client unload the chunk at
 * the given coordinates on the client side.
 */
@Immutable
public class PlayOutUnloadChunk extends PacketOut {
    /**
     * The chunk x
     */
    private final int x;
    /**
     * The chunk z
     */
    private final int z;

    public PlayOutUnloadChunk(int x, int z) {
        super(PlayOutUnloadChunk.class);
        this.x = x;
        this.z = z;
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.z);
    }
}