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
