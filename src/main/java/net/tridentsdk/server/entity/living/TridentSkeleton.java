package net.tridentsdk.server.entity.living;

import net.tridentsdk.Position;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.living.Skeleton;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.event.entity.EntityDamageEvent;
import net.tridentsdk.server.entity.TridentLivingEntity;
import net.tridentsdk.window.inventory.Item;

import java.util.UUID;

/**
 * Represents a skeleton
 *
 * @author The TridentSDK Team
 */
public class TridentSkeleton extends TridentLivingEntity implements Skeleton {
    public TridentSkeleton(UUID uuid, Position spawnPosition) {
        super(uuid, spawnPosition);
    }

    @Override
    public boolean isWither() {
        return false;
    }

    @Override
    public Item[] equipment() {
        return new Item[0];
    }

    @Override
    public void setEquipment(Item[] stack) {

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
        return EntityType.SKELETON;
    }
}
