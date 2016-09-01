package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.net.NetData;
import net.tridentsdk.server.packet.PacketIn;
import net.tridentsdk.server.player.TridentPlayer;

/**
 * @author TridentSDK
 * @since 0.5-alpha
 */
public class PlayInAnimation extends PacketIn {

    public PlayInAnimation() {
        super(PlayInAnimation.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        int animation = NetData.rvint(buf);

        PlayOutAnimation packet = new PlayOutAnimation(client.player(), animation == 0 ? PlayOutAnimation.AnimationType.SWING_MAIN_ARM : PlayOutAnimation.AnimationType.SWING_OFFHAND);
        TridentServer.instance().players().forEach(p -> {
            if (p != client.player()) {
                ((TridentPlayer) p).net().sendPacket(packet);
            }
        });
    }

}
