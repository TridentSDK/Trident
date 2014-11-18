/*
 *     Trident - A Multithreaded Server Alternative
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
package net.tridentsdk.server.world;

import net.tridentsdk.world.ChunkLocation;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A (simple) cache for RegionFiles
 */
public class RegionFileCache {

    final Map<Path, RegionFile> regionFiles = new ConcurrentHashMap<>();

    public RegionFile getRegionFile(Path worldPath, ChunkLocation location) {
        Path regionPath = Paths.get(worldPath.toString(), "region", WorldUtils.getRegionFile(location));

        return this.regionFiles.get(regionPath);
    }
}
