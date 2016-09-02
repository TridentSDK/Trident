package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.net.NetData;
import net.tridentsdk.server.packet.PacketOut;
import net.tridentsdk.server.player.TridentPlayer;

/**
 * @author TridentSDK
 * @since 0.5-alpha
 */
public class PlayOutAnimation extends PacketOut {

    private TridentPlayer player;
    private AnimationType animationType;

    public PlayOutAnimation(TridentPlayer player, AnimationType animationType) {
        super(PlayOutAnimation.class);
        this.player = player;
        this.animationType = animationType;
    }

    @Override
    public void write(ByteBuf buf) {
        NetData.wvint(buf, player.getId());
        buf.writeByte(animationType.ordinal());
    }

    public enum AnimationType {

        SWING_MAIN_ARM,
        TAKE_DAMAGE,
        LEAVE_BED,
        SWING_OFFHAND,
        CRITICAL_EFFECT,
        MAGIC_CRITICAL_EFFECT;

    }

}
