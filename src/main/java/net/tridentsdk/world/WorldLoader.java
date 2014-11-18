/*
 *     TridentSDK - A Minecraft Server API
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.world;

import net.tridentsdk.api.world.*;
import net.tridentsdk.api.world.Chunk;

public interface WorldLoader {
    net.tridentsdk.api.world.World load(String world);

    void save(net.tridentsdk.api.world.World world);

    boolean worldExists(String world);

    boolean chunkExists(net.tridentsdk.api.world.World world, int x, int z);

    boolean chunkExists(net.tridentsdk.api.world.World world, ChunkLocation location);

    net.tridentsdk.api.world.Chunk loadChunk(net.tridentsdk.api.world.World world, int x, int z);

    net.tridentsdk.api.world.Chunk loadChunk(net.tridentsdk.api.world.World world, ChunkLocation location);

    void saveChunk(Chunk chunk);
}
