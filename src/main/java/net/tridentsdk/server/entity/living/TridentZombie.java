package net.tridentsdk.server.entity.living;

import net.tridentsdk.Position;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.living.Zombie;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.event.entity.EntityDamageEvent;
import net.tridentsdk.window.inventory.Item;

import java.util.UUID;

/**
 * Represents a zombie
 *
 * @author The TridentSDK Team
 */
public class TridentZombie extends TridentLivingEntity implements Zombie {
    public TridentZombie(UUID uuid, Position spawnPosition) {
        super(uuid, spawnPosition);
    }

    @Override
    public boolean isVillager() {
        return false;
    }

    @Override
    public boolean isBaby() {
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
        return EntityType.ZOMBIE;
    }
}
