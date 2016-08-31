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
public class PlayInEntityAction extends PacketIn {

    public PlayInEntityAction() {
        super(PlayInEntityAction.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        int entityId = NetData.rvint(buf);
        int actionId = NetData.rvint(buf);
        int jumpBoost = NetData.rvint(buf);

        switch (actionId) {
            case 0: // start crouching
                client.player().getMetadata().setCrouched(true);
                break;
            case 1: // stop crouching
                client.player().getMetadata().setCrouched(false);
                break;
            case 2: // leave bed
                // TODO
                break;
            case 3: // start sprinting
                client.player().getMetadata().setSprinting(true);
                break;
            case 4: // stop sprinting
                client.player().getMetadata().setSprinting(false);
                break;
            case 5: // start jump w/ horse
                break;
            case 6: // stop jump w/ horse
                break;
            case 7: // open horse inventory
                break;
            case 8: // start flying w/ elytra
                break;
            default:
                break;
        }

        PlayOutEntityMetadata metadataPacket = new PlayOutEntityMetadata(client.player());
        TridentServer.instance().players().forEach(p -> {
            if (p != client.player()) {
                ((TridentPlayer) p).net().sendPacket(metadataPacket);
            }
        });
    }

}
