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
import net.tridentsdk.api.world.ChunkLocation;

import java.util.Collections;
import java.util.Map;

public class ChunkSnapshot {

    private final Map<net.tridentsdk.api.world.ChunkLocation, net.tridentsdk.api.world.Chunk> chunks;

    public ChunkSnapshot(Map<net.tridentsdk.api.world.ChunkLocation, net.tridentsdk.api.world.Chunk> snapshot) {
        this.chunks = Collections.unmodifiableMap(snapshot);
    }

    public net.tridentsdk.api.world.Chunk getChunkAt(net.tridentsdk.api.world.ChunkLocation location) {
        return this.chunks.get(location);
    }

    public net.tridentsdk.api.world.Chunk getChunkAt(int x, int z) {
        return this.getChunkAt(new ChunkLocation(x, z));
    }
}
