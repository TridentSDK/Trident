package net.tridentsdk.server.entity.vehicle;

import net.tridentsdk.Position;
import net.tridentsdk.base.BlockSnapshot;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.entity.vehicle.CommandMinecart;

import java.util.UUID;

/**
 * Represents a minecart that holds a command block
 *
 * @author The TridentSDK Team
 */
public class TridentCmdMinecart extends TridentMinecart implements CommandMinecart {
    public TridentCmdMinecart(UUID uuid, Position spawnPosition) {
        super(uuid, spawnPosition);
    }

    @Override
    public BlockSnapshot commandBlockState() {
        return null;
    }

    @Override
    public EntityType type() {
        return EntityType.COMMAND_MINECART;
    }
}
