package net.tridentsdk.server.entity;

import net.tridentsdk.Position;
import net.tridentsdk.entity.decorate.Ageable;
import net.tridentsdk.server.data.ProtocolMetadata;

import java.util.UUID;

public abstract class TridentAgeable extends TridentLivingEntity implements Ageable {
    protected volatile int age;

    public TridentAgeable(UUID id, Position spawnLocation) {
        super(id, spawnLocation);
    }

    @Override
    protected void updateProtocolMeta() {
        protocolMeta.setMeta(12, ProtocolMetadata.MetadataType.BYTE, age);
    }

    @Override
    public void setAge(int ticks) {
        this.age = ticks;
    }

    @Override
    public int age() {
        return age;
    }
}
