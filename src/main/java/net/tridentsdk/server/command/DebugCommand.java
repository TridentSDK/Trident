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
import net.tridentsdk.base.Substance;
import net.tridentsdk.command.Command;
import net.tridentsdk.command.CommandListener;
import net.tridentsdk.command.CommandSource;
import net.tridentsdk.command.CommandSourceType;
import net.tridentsdk.command.annotation.AllowedSourceTypes;
import net.tridentsdk.command.annotation.PermissionRequired;
import net.tridentsdk.doc.Debug;
import net.tridentsdk.inventory.Item;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.play.PlayOutChunk;
import net.tridentsdk.server.packet.play.PlayOutDestroyEntities;
import net.tridentsdk.server.packet.play.PlayOutTabListItem;
import net.tridentsdk.server.player.RecipientSelector;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.world.TridentChunk;
import net.tridentsdk.ui.bossbar.BossBar;
import net.tridentsdk.ui.bossbar.BossBarColor;
import net.tridentsdk.ui.bossbar.BossBarDivision;
import net.tridentsdk.ui.chat.ChatColor;
import net.tridentsdk.ui.chat.ChatComponent;
import net.tridentsdk.ui.chat.HoverEvent;
import net.tridentsdk.ui.title.Title;

import javax.annotation.concurrent.Immutable;
import java.util.Collections;

@Immutable
@Debug
public class DebugCommand implements CommandListener {

    @Command(name = "debug", help = "/debug <chunks|bossbars|title|cleartitle|chat|rain|change>", desc = "Secret debug command for devs")
    @AllowedSourceTypes(CommandSourceType.PLAYER)
    @PermissionRequired("trident.debug")
    public void debug(CommandSource source, String[] args, String mode) {
        TridentPlayer player = (TridentPlayer) source;
        NetClient client = player.net();

        if (mode.equals("chunks")) {
            Position playerPosition = player.getPosition();
            int chunkLoadRadius = 3;

            for (int x = playerPosition.getChunkX() - chunkLoadRadius; x <= playerPosition.getChunkX() + chunkLoadRadius; x++) {
                for (int z = playerPosition.getChunkZ() - chunkLoadRadius; z <= playerPosition.getChunkZ() + chunkLoadRadius; z++) {
                    TridentChunk chunk = (TridentChunk) playerPosition.getWorld().getChunkAt(x, z);
                    client.sendPacket(new PlayOutChunk(chunk));
                }
            }
        } else if (mode.equals("bossbars")) {
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
        } else if (mode.equals("title")) {
            Title title = Title.newTitle();

            title.setHeader(ChatComponent.create().setColor(ChatColor.AQUA).setText("henlo player"));
            title.setSubtitle(ChatComponent.create().setColor(ChatColor.GOLD).setText("hello u STINKY PLAYER"));
            title.setFadeIn(0);
            title.setStay(600);
            title.setFadeOut(0);

            player.sendTitle(title);
        } else if (mode.equals("cleartitle")) {
            player.resetTitle();
        } else if (mode.equals("chat")) {
            player.sendMessage(ChatComponent.create().setText("What is this").setHoverEvent(
                    HoverEvent.item(Item.newItem(Substance.STONE, 30, (byte) 1))));
        } else if (mode.equals("rain")) {
            player.getWorld().getWeather().beginRaining();
            player.getWorld().getWeather().beginThunder();
        } else if (mode.equals("change")) {
            PlayOutTabListItem.RemovePlayer removePlayer = PlayOutTabListItem.removePlayerPacket();
            removePlayer.removePlayer(player.getUuid());

            PlayOutTabListItem.AddPlayer addPlayer = PlayOutTabListItem.addPlayerPacket();
            addPlayer.addPlayer(player.getUuid(), "Im_&*!@#$``~", player.getGameMode(), 0, player.getTabListName(),
                    Collections.emptyList());

            RecipientSelector.whoCanSee(player, false, new PlayOutDestroyEntities(Collections.singletonList(player)),
                    addPlayer);
            RecipientSelector.whoCanSee(player, true, player.getSpawnPacket());
        }
    }
}