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
package net.tridentsdk.server.chunk;

import com.google.common.collect.Sets;
import net.tridentsdk.Trident;
import net.tridentsdk.base.Position;
import net.tridentsdk.config.ConfigSection;
import net.tridentsdk.docs.Policy;
import net.tridentsdk.server.packets.play.out.PacketPlayOutChunkData;
import net.tridentsdk.server.packets.play.out.PacketPlayOutMapChunkBulk;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.world.TridentChunk;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.world.ChunkLocation;

import javax.annotation.concurrent.GuardedBy;
import java.util.HashSet;

/**
 *
 */
public class ChunkLocationSet {
    private static final ConfigSection tridentCfg = Trident.config().getConfigSection("performance");
    private static final int MAX_CHUNKS = tridentCfg.getInt("max-chunks-player", 441);
    private static final int CLEAN_ITERATIONS = tridentCfg.getInt("chunk-clean-iterations-player", 2);

    @GuardedBy("knownChunks")
    private final HashSet<ChunkLocation> knownChunks = Sets.newHashSet();
    private final TridentPlayer player;
    private final TridentWorld world;

    public ChunkLocationSet(TridentPlayer player) {
        this.player = player;
        this.world = ((TridentWorld) player.world());
    }

    public void clean(int distance) {
        synchronized (knownChunks) {
            for (int i = 0; i < CLEAN_ITERATIONS; i++) {
                int size = knownChunks.size();
                if (size > MAX_CHUNKS) {
                    clean0(distance, size);
                }
            }
        }
    }

    @Policy("holds knownChunks")
    private void clean0(int viewDist, int size) {
        Position pos = player.position();
        int x = (int) pos.x() / 16;
        int z = (int) pos.z() / 16;

        int removed = 0;
        for (ChunkLocation location : knownChunks) {
            if (MAX_CHUNKS > (size - removed)) return;

            int cx = location.x();
            int cz = location.z();

            int abs = Math.max(cx, x) - Math.min(cx, x);
            int abs1 = Math.max(cz, z) - Math.min(cz, z);

            if (abs >= viewDist || abs1 >= viewDist) {
                player.connection().sendPacket(new PacketPlayOutChunkData(new byte[0], location, true, (short) 0));
                knownChunks.remove(location);
                removed++;
            }
        }
    }

    public void update(int viewDistance) {
        int centX = ((int) Math.floor(player.position().x())) >> 4;
        int centZ = ((int) Math.floor(player.position().z())) >> 4;

        PacketPlayOutMapChunkBulk bulk = new PacketPlayOutMapChunkBulk();
        HashSet<TridentChunk> set = new HashSet<>();

        synchronized (knownChunks) {
            for (int x = (centX - viewDistance / 2); x <= (centX + viewDistance / 2); x += 1) {
                for (int z = (centZ - viewDistance / 2); z <= (centZ + viewDistance / 2); z += 1) {
                    for (int i = x - 1; i <= x + 1; i++) {
                        for (int j = z - 1; j <= z + 1; j++) {
                            ChunkLocation loc = ChunkLocation.create(i, j);
                            if (knownChunks.contains(loc)) continue;

                            TridentChunk chunk = world.chunkAt(loc, true);
                            if (i == x && j == z) {
                                set.add(chunk);
                            }
                        }
                    }
                }
            }

            for (TridentChunk chunk : set) {
                if (knownChunks.add(chunk.location())) {
                    bulk.addEntry(chunk.asPacket());
                }

                if (bulk.size() >= 1845152) {
                    player.connection().sendPacket(bulk);
                    bulk = new PacketPlayOutMapChunkBulk();
                }
            }

            if (bulk.hasEntries()) {
                player.connection().sendPacket(bulk);
            }
        }
    }

    public void clear() {
        ChunkHandler handler = world.chunkHandler();
        synchronized (knownChunks) {
            for (ChunkLocation location : knownChunks) {
                handler.apply(location, CRefCounter::releaseStrong);
            }

            knownChunks.clear();
        }
    }
}
