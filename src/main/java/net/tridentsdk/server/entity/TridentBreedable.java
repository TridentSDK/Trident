package net.tridentsdk.server.entity;

import net.tridentsdk.Position;
import net.tridentsdk.meta.nbt.CompoundTag;
import net.tridentsdk.meta.nbt.IntTag;

import java.util.UUID;

public abstract class TridentBreedable extends TridentAgeable {
    protected volatile int loveTimeout;
    protected volatile boolean inLove;
    protected volatile boolean canBreed = false;

    public TridentBreedable(UUID id, Position spawnLocation) {
        super(id, spawnLocation);
    }

    @Override
    public boolean canBreed() {
        return canBreed;
    }

    @Override
    public boolean isInLove() {
        return inLove;
    }

    @Override
    public void doLoad(CompoundTag tag) {
        this.loveTimeout = ((IntTag) tag.getTag("InLove")).value();
        this.inLove = false;
    }
}
