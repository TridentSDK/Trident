package net.tridentsdk.server.entity.living;

import net.tridentsdk.Position;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.living.Rabbit;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.entity.types.RabbitType;
import net.tridentsdk.event.entity.EntityDamageEvent;

import java.util.UUID;

/**
 * Represents a rabbit
 *
 * @author The TridentSDK Team
 */
public class TridentRabbit extends TridentLivingEntity implements Rabbit {
    public TridentRabbit(UUID uuid, Position spawnPosition) {
        super(uuid, spawnPosition);
    }

    @Override
    public RabbitType breed() {
        return null;
    }

    @Override
    public boolean isHostile() {
        return false;
    }

    @Override
    public EntityDamageEvent lastDamageEvent() {
        return null;
    }

    @Override
    public Player lastPlayerDamager() {
        return null;
    }

    @Override
    public EntityType type() {
        return EntityType.RABBIT;
    }
}
