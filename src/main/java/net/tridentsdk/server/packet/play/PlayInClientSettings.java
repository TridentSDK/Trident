/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.net.NetData;
import net.tridentsdk.server.packet.PacketIn;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.player.TridentPlayerMeta;
import net.tridentsdk.ui.chat.ClientChatMode;

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
        byte renderDistance = buf.readByte();
        ClientChatMode chatMode = ClientChatMode.of(NetData.rvint(buf));
        boolean chatColors = buf.readBoolean();
        byte skinFlags = buf.readByte();
        int mainHand = buf.readByte();

        TridentPlayer player = client.getPlayer();
        TridentPlayerMeta metadata = player.getMetadata();
        player.setRenderDistance(renderDistance);
        player.setLocale(locale);
        player.setChatColors(chatColors);
        player.setChatMode(chatMode);
        metadata.setSkinFlags(skinFlags);
        metadata.setLeftHandMain(mainHand == 0);
        player.updateMetadata();
        player.resumeLogin();
    }
}