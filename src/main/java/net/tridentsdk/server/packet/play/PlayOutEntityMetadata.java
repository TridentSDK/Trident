package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.entity.TridentEntity;
import net.tridentsdk.server.net.NetData;
import net.tridentsdk.server.packet.PacketOut;

/**
 * @author TridentSDK
 * @since 0.5-alpha
 */
public class PlayOutEntityMetadata extends PacketOut {

    private final TridentEntity entity;

    public PlayOutEntityMetadata(TridentEntity entity) {
        super(PlayOutEntityMetadata.class);
        this.entity = entity;
    }

    @Override
    public void write(ByteBuf buf) {
        NetData.wvint(buf, entity.id());
        entity.getMetadata().getMetadata().write(buf);
    }

}
