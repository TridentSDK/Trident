package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.base.Position;
import net.tridentsdk.server.entity.TridentEntity;
import net.tridentsdk.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;

import static net.tridentsdk.server.net.NetData.convertAngle;
import static net.tridentsdk.server.net.NetData.wvint;

/**
 * Sent by the server whenever a player needs to be moved
 * more than 4 blocks.
 */
@Immutable
public class PlayOutTeleport extends PacketOut {
    private final int eid;
    private final Position position;
    private final boolean onGround;

    public PlayOutTeleport(TridentEntity entity) {
        super(PlayOutTeleport.class);
        this.eid = entity.getId();
        this.position = entity.getPosition();
        this.onGround = entity.isOnGround();
    }

    @Override
    public void write(ByteBuf buf) {
        wvint(buf, this.eid);
        buf.writeDouble(this.position.getX());
        buf.writeDouble(this.position.getY());
        buf.writeDouble(this.position.getZ());
        buf.writeByte(convertAngle(this.position.getYaw()));
        buf.writeByte(convertAngle(this.position.getPitch()));
        buf.writeBoolean(this.onGround);
    }
}