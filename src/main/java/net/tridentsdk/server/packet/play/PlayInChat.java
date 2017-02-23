/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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
import net.tridentsdk.base.Position;
import net.tridentsdk.chat.*;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.PacketIn;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.world.TridentChunk;
import net.tridentsdk.ui.bossbar.BossBar;
import net.tridentsdk.ui.bossbar.BossBarColor;
import net.tridentsdk.ui.bossbar.BossBarDivision;

import javax.annotation.concurrent.Immutable;

import static net.tridentsdk.server.net.NetData.rstr;

/**
 * This packet is received by the server when a player
 * sends
 * a chat message.
 */
@Immutable
public final class PlayInChat extends PacketIn {
    public PlayInChat() {
        super(PlayInChat.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        TridentPlayer player = client.getPlayer();
        String msg = rstr(buf);

        ChatComponent chat = ChatComponent.create()
                .setTranslate("chat.type.text")
                .addWith(ChatComponent.create()
                        .setText(player.getName())
                        .setClickEvent(ClickEvent.of(ClickAction.SUGGEST_COMMAND, "/tell " + player.getName() + " ")))
                .addWith(msg);
        TridentPlayer.getPlayers().values().forEach(p -> p.sendMessage(chat, ChatType.CHAT));

        if(msg.toLowerCase().equals("chunks")){
            Position playerPosition = player.getPosition();
            int chunkLoadRadius = 3;

            for (int x = playerPosition.getChunkX() - chunkLoadRadius; x <= playerPosition.getChunkX() + chunkLoadRadius; x++) {
                for (int z = playerPosition.getChunkZ() - chunkLoadRadius; z <= playerPosition.getChunkZ() + chunkLoadRadius; z++) {
                    TridentChunk chunk = (TridentChunk) playerPosition.world().getChunkAt(x, z);
                    client.sendPacket(new PlayOutChunk(chunk));
                }
            }
        }

        if (msg.toLowerCase().equals("bossbars")) {
            int i = 0;
            for (String word : "I hate my life".split(" ")) {
                BossBar bb = BossBar.newBossBar();

                bb.setTitle(ChatComponent.text(word).setColor(ChatColor.of((char) ('a' + i))));
                bb.setColor(BossBarColor.values()[i]);
                bb.setDivision(BossBarDivision.values()[i++]);
                bb.setHealth(i * .25f);
                bb.setDarkenSky(false);
                bb.setDragonBar(false);

                player.addBossBar(bb);
            }
        }
    }
}
