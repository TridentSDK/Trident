package net.tridentsdk.server.entity;

import net.tridentsdk.Position;
import net.tridentsdk.entity.decorate.Tameable;
import net.tridentsdk.server.data.ProtocolMetadata;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.util.TridentLogger;

import java.util.UUID;

public abstract class TridentTameable extends TridentAgeable implements Tameable {

    protected volatile byte tameData;
    protected volatile UUID owner;

    protected TridentTameable(UUID id, Position spawnLocation) {
        super(id, spawnLocation);

        this.tameData = 0;
    }

    @Override
    protected void updateProtocolMeta() {
        protocolMeta.setMeta(16, ProtocolMetadata.MetadataType.BYTE, tameData);
        protocolMeta.setMeta(17, ProtocolMetadata.MetadataType.STRING,
                owner == null ? "" : TridentPlayer.getPlayer(owner).name());

    }

    @Override
    public boolean isSitting() {
        return (tameData & 1) == 1;
    }

    @Override
    public UUID owner() {
        return owner;
    }

    @Override
    public boolean isTamed() {
        return (tameData & 4) == 4;
    }

    public void setTame(final UUID owner) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if(TridentPlayer.getPlayer(owner) == null) {
                    TridentLogger.error(new IllegalArgumentException("No player found with provided UUID!"));
                    return;
                }

                TridentTameable.this.owner = owner;
                tameData |= 4;
            }
        });
    }
}
