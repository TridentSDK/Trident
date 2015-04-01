package net.tridentsdk.server.entity.vehicle;

import net.tridentsdk.Position;
import net.tridentsdk.base.BlockSnapshot;
import net.tridentsdk.entity.traits.EntityProperties;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.entity.vehicle.SpawnerMinecart;
import net.tridentsdk.server.entity.TridentEntity;

import java.util.UUID;

/**
 * Represents a minecart that holds a spawner block
 *
 * @author The TridentSDK Team
 */
public class TridentSpawnerMinecart extends TridentEntity implements SpawnerMinecart {
    public TridentSpawnerMinecart(UUID uuid, Position spawnPosition) {
        super(uuid, spawnPosition);
    }

    @Override
    public EntityType spawnType() {
        return null;
    }

    @Override
    public EntityProperties appliedProperties() {
        return null;
    }

    @Override
    public BlockSnapshot displayTitle() {
        return null;
    }

    @Override
    public void setDisplayTile(BlockSnapshot blockState) {

    }

    @Override
    public int titleOffset() {
        return 0;
    }

    @Override
    public void setDisplayTileOffset(int offset) {

    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public EntityType type() {
        return EntityType.SPAWNER_MINECART;
    }
}
