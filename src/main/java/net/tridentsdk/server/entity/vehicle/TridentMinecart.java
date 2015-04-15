package net.tridentsdk.server.entity.vehicle;

import net.tridentsdk.Position;
import net.tridentsdk.base.BlockSnapshot;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.entity.vehicle.MinecartBase;
import net.tridentsdk.server.entity.TridentEntity;

import java.util.UUID;

/**
 * Represents a minecart
 *
 * @author The TridentSDK Team
 */
public class TridentMinecart extends TridentEntity implements MinecartBase {
    public TridentMinecart(UUID uuid, Position spawnPosition) {
        super(uuid, spawnPosition);
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
        return EntityType.MINECART;
    }
}
