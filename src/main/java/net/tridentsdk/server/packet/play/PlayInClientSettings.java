package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.chat.ClientChatMode;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.net.NetData;
import net.tridentsdk.server.packet.PacketIn;

/**
 * @author TridentSDK
 * @since 0.5-alpha
 */
public class PlayInClientSettings extends PacketIn {

    public PlayInClientSettings() {
        super(PlayInClientSettings.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        String locale = NetData.rstr(buf);
        byte viewDistance = buf.readByte();
        ClientChatMode chatMode = ClientChatMode.of(NetData.rvint(buf));
        boolean chatColors = buf.readBoolean();
        byte skinFlags = buf.readByte();
        int mainHand = buf.readByte();

        Player player = client.player();
        player.getMetadata().setSkinFlags(skinFlags);
        player.getMetadata().setLeftHandMain(mainHand == 0);
        player.updateMetadata();
    }

}
