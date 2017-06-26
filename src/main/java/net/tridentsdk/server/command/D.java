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
package net.tridentsdk.server.command;

import net.tridentsdk.base.Position;
import net.tridentsdk.command.*;
import net.tridentsdk.doc.Debug;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.play.PlayOutChunk;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.world.TridentChunk;
import net.tridentsdk.ui.bossbar.BossBar;
import net.tridentsdk.ui.bossbar.BossBarColor;
import net.tridentsdk.ui.bossbar.BossBarDivision;
import net.tridentsdk.ui.chat.ChatColor;
import net.tridentsdk.ui.chat.ChatComponent;
import net.tridentsdk.ui.title.Title;

import javax.annotation.concurrent.Immutable;

@Immutable
@Debug
public class D implements CmdListener {
    @Cmd(name = "d", help = "/debug <chunks|bossbars|title|cleartitle>",
            desc = "Secret debug command for devs")
    @Constrain(value = SourceConstraint.class, type = ConstraintType.SOURCE, src = CmdSourceType.PLAYER)
    @Constrain(value = MinArgsConstraint.class, type = ConstraintType.INT, integer = 1)
    @Constrain(value = PermsConstraint.class, type = ConstraintType.STRING, str = "trident.debug")
    public void debug(String label, CmdSource source, String[] args) {
        TridentPlayer player = (TridentPlayer) source;
        NetClient client = player.net();
        String msg = args[0].toLowerCase();

        if (msg.equals("chunks")) {
            Position playerPosition = player.getPosition();
            int chunkLoadRadius = 3;

            for (int x = playerPosition.getChunkX() - chunkLoadRadius; x <= playerPosition.getChunkX() + chunkLoadRadius; x++) {
                for (int z = playerPosition.getChunkZ() - chunkLoadRadius; z <= playerPosition.getChunkZ() + chunkLoadRadius; z++) {
                    TridentChunk chunk = (TridentChunk) playerPosition.world().getChunkAt(x, z);
                    client.sendPacket(new PlayOutChunk(chunk));
                }
            }
        }

        if (msg.equals("bossbars")) {
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

        if (msg.equals("title")) {
            Title title = Title.newTitle();

            title.setHeader(ChatComponent.create().setColor(ChatColor.AQUA).setText("henlo player"));
            title.setSubtitle(ChatComponent.create().setColor(ChatColor.GOLD).setText("hello u STINKY PLAYER"));
            title.setFadeIn(0);
            title.setStay(600);
            title.setFadeOut(0);

            player.sendTitle(title);
        }

        if (msg.equals("cleartitle")) {
            player.resetTitle();
        }
    }
}