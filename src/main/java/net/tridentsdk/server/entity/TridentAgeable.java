package net.tridentsdk.server.entity;

import net.tridentsdk.Position;
import net.tridentsdk.entity.decorate.Ageable;
import net.tridentsdk.server.data.ProtocolMetadata;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class TridentAgeable extends TridentLivingEntity implements Ageable {
    protected final AtomicInteger age = new AtomicInteger(0);

    public TridentAgeable(UUID id, Position spawnLocation) {
        super(id, spawnLocation);
    }

    @Override
    protected void updateProtocolMeta() {
        protocolMeta.setMeta(12, ProtocolMetadata.MetadataType.BYTE, age.get());
    }

    @Override
    public void setAge(final int ticks) {
        age.set(ticks);
    }

    @Override
    public int age() {
        return age.get();
    }
}
