/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
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
package net.tridentsdk.server.scoreboard;

import net.tridentsdk.base.board.BoardType;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.scoreboard.Scoreboard;
import net.tridentsdk.scoreboard.ScoreboardModule;
import net.tridentsdk.server.packets.play.out.PacketPlayOutDisplayScoreboard;
import net.tridentsdk.server.packets.play.out.PacketPlayOutScoreboardObjective;
import net.tridentsdk.server.packets.play.out.PacketPlayOutUpdateScore;
import net.tridentsdk.server.player.TridentPlayer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TridentScoreboard implements Scoreboard {

    private Map<ScoreboardModule, Integer> modules = new HashMap<>();
    private Set<Player> players = new HashSet<>();
    private String resetSpaces = " ";
    private String spaces = " ";

    @Override
    public void addModule(ScoreboardModule module, int priority) {
        modules.put(module, priority);
        updateBoard(0, true);
    }

    @Override
    public void showToPlayer(Player player) {
        PacketPlayOutScoreboardObjective objective = new PacketPlayOutScoreboardObjective();
        objective.set("name", "Sidebar");
        objective.set("mode", (short) 0);
        objective.set("value", "Sidebar");
        objective.set("type", "integer");
        ((TridentPlayer) player).connection().sendPacket(objective);

        PacketPlayOutDisplayScoreboard display = new PacketPlayOutDisplayScoreboard();
        display.set("boardType", BoardType.SIDEBAR);
        display.set("scoreName", "Sidebar");
        ((TridentPlayer) player).connection().sendPacket(display);

        modules.keySet().stream().forEach(module -> {
            module.permanentItems().stream().forEach(item -> {
                PacketPlayOutUpdateScore update = new PacketPlayOutUpdateScore();
                update.set("itemName", item.value().startsWith("%SPACE%") ? spaces : item.value());
                update.set("type", PacketPlayOutUpdateScore.UpdateType.CREATE);
                update.set("scoreName", "Sidebar");
                update.set("value", item.score());
                ((TridentPlayer) player).connection().sendPacket(update);

                if(item.value().startsWith("%SPACE%")){
                    spaces += " ";
                }
            });

            module.liveItems().stream().forEach(item -> {
                PacketPlayOutUpdateScore update = new PacketPlayOutUpdateScore();
                update.set("itemName", item.value().startsWith("%SPACE%") ? spaces : item.value());
                update.set("type", PacketPlayOutUpdateScore.UpdateType.CREATE);
                update.set("scoreName", "Sidebar");
                update.set("value", item.score());
                ((TridentPlayer) player).connection().sendPacket(update);

                if(item.value().startsWith("%SPACE%")){
                    spaces += " ";
                }
            });
        });

        players.add(player);
    }

    public void updateBoard(int tick) {
        updateBoard(tick, false);
    }

    public void updateBoard(int tick, boolean reset) {
        if(reset){
            resetSpaces = " ";
        }

        spaces = resetSpaces;
        modules.keySet().stream().filter(module -> module.update(tick)).forEach(module -> {
            module.removedItems().stream().forEach(item -> {
                PacketPlayOutUpdateScore update = new PacketPlayOutUpdateScore();
                update.set("itemName", item.value());
                update.set("type", PacketPlayOutUpdateScore.UpdateType.REMOVE);
                update.set("scoreName", "Sidebar");
                players.stream().forEach(player -> ((TridentPlayer) player).connection().sendPacket(update));
            });

            module.updatedItems().stream().forEach(item -> {
                PacketPlayOutUpdateScore update = new PacketPlayOutUpdateScore();
                update.set("itemName", item.value().startsWith("%SPACE%") ? spaces : item.value());
                update.set("type", PacketPlayOutUpdateScore.UpdateType.CREATE);
                update.set("scoreName", "Sidebar");
                update.set("value", item.score());
                players.stream().forEach(player -> ((TridentPlayer) player).connection().sendPacket(update));

                if(item.value().startsWith("%SPACE%")) {
                    spaces += " ";

                    if(reset) {
                        resetSpaces += " ";
                    }
                }
            });
        });
    }

}
