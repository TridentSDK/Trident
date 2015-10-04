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

package net.tridentsdk.server.command;

import net.tridentsdk.concurrent.ScheduledRunnable;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.plugin.annotation.CommandDesc;
import net.tridentsdk.plugin.cmd.Command;
import net.tridentsdk.registry.Registered;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.world.TridentChunk;
import net.tridentsdk.world.Chunk;

@CommandDesc(name = "send", permission = "trident.send", aliases = "")
public class SendCommand extends Command {

    @Override
    public void handlePlayer(Player player, String arguments, String alias) {
        Chunk chunk = player.position().chunk();
        chunk.generate();
        tell(player, chunk);

        Registered.tasks().asyncRepeat(null, new ScheduledRunnable() {
            @Override
            public void run() {
                player.world().chunkAt(chunk.location(), true);
                tell(player, chunk);
                ((TridentChunk) chunk).printHeld();

                if (((TridentChunk) chunk).isGen()) {
                    player.sendMessage("CHUNK " + chunk.location() + " IS GENERATED");
                    ((TridentPlayer) player).sendChunks(7);
                    cancel();
                }
            }
        }, 0L, 20L);
    }

    void tell(Player player, Chunk chunk) {
        player.sendMessage(chunk.location() + " is " + ((TridentChunk) chunk).isGen());
    }
}