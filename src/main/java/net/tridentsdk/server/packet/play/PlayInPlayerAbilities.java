package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.PacketIn;
import net.tridentsdk.server.player.TridentPlayer;

/**
 * @author Nick Robson
 */
public class PlayInPlayerAbilities extends PacketIn {

    public PlayInPlayerAbilities() {
        super(PlayInPlayerAbilities.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        byte flags = buf.readByte();

        boolean isGod = (flags & 0x08) != 0;
        boolean canFly = (flags & 0x04) != 0;
        boolean isFlying = (flags & 0x02) != 0;
        boolean isCreative = (flags & 0x01) != 0;

        float flyingSpeed = buf.readFloat();
        float walkingSpeed = buf.readFloat();

        // NOTE: We have to be very careful here, since a hacked client can easily send these things.

        TridentPlayer player = client.getPlayer();

        if (player.canFly()) {
            player.setFlying(isFlying);
        } else {
            player.setFlying(false);
        }

        client.sendPacket(new PlayOutPlayerAbilities(player));
    }
}
