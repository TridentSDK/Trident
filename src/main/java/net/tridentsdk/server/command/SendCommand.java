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

import net.tridentsdk.entity.living.Player;
import net.tridentsdk.plugin.annotation.CommandDesc;
import net.tridentsdk.plugin.cmd.Command;
import net.tridentsdk.world.Chunk;
import net.tridentsdk.world.ChunkLocation;

@CommandDesc(name = "send", permission = "trident.send", aliases = "")
public class SendCommand extends Command {
    @Override
    public void handlePlayer(Player player, String arguments, String alias) {
        ChunkLocation location = ChunkLocation.create(((int) player.position().x()) >> 4, ((int) player.position().z()) >> 4);
        Chunk chunk = player.world().chunkAt(location, false);
        if (chunk == null) {
            throw new IllegalStateException(location.toString());
        } else {
            chunk.generate();
        }
    }
}