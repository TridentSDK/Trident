package net.tridentsdk.server.world.change;

import net.tridentsdk.base.Position;
import net.tridentsdk.base.Substance;
import net.tridentsdk.server.data.RecordBuilder;
import net.tridentsdk.server.packets.play.out.PacketPlayOutMultiBlockChange;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.world.TridentChunk;
import net.tridentsdk.server.world.WorldUtils;
import net.tridentsdk.world.ChunkLocation;
import net.tridentsdk.world.MassChange;
import net.tridentsdk.world.World;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A MassChange that is thread-safe, unlike the default
 *
 * @author The TridentSDK Team
 */
public class ThreadSafeChange implements MassChange {
    private final World world;
    private volatile boolean committed = false;
    private Queue<BlockChange> changes = new ConcurrentLinkedQueue<>();

    public ThreadSafeChange(World world) {
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
