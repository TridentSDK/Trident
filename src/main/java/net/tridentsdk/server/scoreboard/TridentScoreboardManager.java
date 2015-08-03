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

import net.tridentsdk.concurrent.ScheduledRunnable;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.registry.Registered;
import net.tridentsdk.scoreboard.Scoreboard;
import net.tridentsdk.scoreboard.ScoreboardManager;

import java.util.HashMap;

public class TridentScoreboardManager implements ScoreboardManager {

    private TridentScoreboard globalScoreboard = new TridentScoreboard();
    private HashMap<Player, TridentScoreboard> playerScoreboards = new HashMap<>();

    public TridentScoreboardManager(){
        Registered.tasks().asyncRepeat(null, new ScheduledRunnable() {
            private int tick = 1;
            @Override
            public void run(){
                globalScoreboard.updateBoard(tick);
                playerScoreboards.values().stream().distinct().forEach(board -> board.updateBoard(tick));
                tick++;
            }
        }, 0, 1);
    }

    @Override
    public Scoreboard getGlobalScoreboard(){
        return globalScoreboard;
    }

    @Override
    public Scoreboard getPlayerScoreboard(Player player){
        if(!playerScoreboards.containsKey(player)){
            playerScoreboards.put(player, new TridentScoreboard());
        }

        return playerScoreboards.get(player);
    }

}
