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
package net.tridentsdk.server.world.change;

import net.tridentsdk.base.Position;
import net.tridentsdk.base.Substance;
import net.tridentsdk.server.data.RecordBuilder;
import net.tridentsdk.server.packets.play.out.PacketPlayOutMultiBlockChange;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.world.TridentChunk;
import net.tridentsdk.server.world.WorldUtils;
import net.tridentsdk.world.ChunkLocation;
import net.tridentsdk.world.World;
import net.tridentsdk.world.MassChange;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.*;

/**
 * The default implementation of MassChange, should be suitable for most usage cases
 *
 * <p>Should only be used by one thread, if needed by more than one, make two separate changes instead</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * MassChange change = new DefaultMassChange(...);
 * change.setBlock(...);
 * change.setBlock(...);
 * .
 * .
 * .
 * change.commitChanges()}</pre>
 */
@NotThreadSafe
public class DefaultMassChange implements MassChange {
    private final World world;
    private boolean committed = false;
    private List<BlockChange> changes = new LinkedList<>();

    public DefaultMassChange(World world) {
        this.world = world;
    }

    @Override
    public void setBlock(int x, int y, int z, int id) throws IllegalStateException {
        setBlock(x, y, z, id, (byte) 0);
    }

    @Override
    public void setBlock(int x, int y, int z, Substance substance) throws IllegalStateException {
        setBlock(x, y, z, substance.id(), (byte) 0);
    }

    @Override
    public void setBlock(int x, int y, int z, Substance substance, byte data) throws IllegalStateException {
        setBlock(x, y, z, substance.id(), data);
    }

    @Override
    public void setBlock(int x, int y, int z, int id, byte data) throws IllegalStateException {
        // real function

        if(committed) {
            throw new IllegalArgumentException("Change has already been committed.");
        }
        changes.add(new BlockChange(x, y, z, (byte) id, data));
    }


    @Override
    public void setBlock(Position coords, int id) throws IllegalStateException {
        setBlock(coords, id, (byte) 0);
    }

    @Override
    public void setBlock(Position coords, int id, byte data) throws IllegalArgumentException,
            IllegalStateException {
        if (coords.world().equals(this.world)) {
            setBlock((int) Math.round(coords.x()),
                    (int) Math.round(coords.y()),
                    (int) Math.round(coords.z()), id, data);
        } else {
            throw new IllegalArgumentException("PositionWritable provided do not match the world that this change is for");
        }
    }

    @Override
    public void setBlock(Position coords, Substance substance) throws IllegalArgumentException,
            IllegalStateException {
        setBlock(coords, substance, (byte) 0);
    }

    @Override
    public void setBlock(Position coords, Substance substance, byte data) throws IllegalArgumentException,
            IllegalStateException {
        setBlock(coords, substance.id(), data);
    }

    @Override
    public boolean commitChanges() throws IllegalStateException {
        if(committed) {
            throw new IllegalArgumentException("Change has already been committed.");
        }

        Map<ChunkLocation, List<BlockChange>> map = new HashMap<>();

        for(BlockChange change : changes) {
            ChunkLocation location = WorldUtils.chunkLocation(change.x(), change.z());
            List<BlockChange> updatedChanges = map.get(location);

            if(updatedChanges == null) {
                updatedChanges = new ArrayList<>();
            }

            updatedChanges.add(change);
            map.put(location, updatedChanges);
        }

        for(Map.Entry<ChunkLocation, List<BlockChange>> entry : map.entrySet()) {
            List<BlockChange> changes = entry.getValue();
            PacketPlayOutMultiBlockChange packet = new PacketPlayOutMultiBlockChange();
            RecordBuilder[] records = new RecordBuilder[changes.size()];
            TridentChunk chunk = (TridentChunk) world.chunkAt(entry.getKey(), false);

            for (int i = 0; i < records.length; i++) {
                BlockChange change = changes.get(i);

                records[i] = new RecordBuilder().setBlockId(change.id())
                        .setX((byte) change.x())
                        .setY((byte) change.y())
                        .setZ((byte) change.z())
                        .setData(change.data());
                chunk.setAt(change.x(), change.y(), change.z(), Substance.fromId(change.id()),
                        change.data(), (byte) 255, (byte) 15);
            }

            packet.set("records", records).set("chunkLocation", entry.getKey());
            TridentPlayer.sendAll(packet);
        }

        return true;
    }
}
