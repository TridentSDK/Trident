package net.tridentsdk.server.entity.living;

import net.tridentsdk.Position;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.living.Villager;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.entity.types.VillagerCareer;
import net.tridentsdk.entity.types.VillagerProfession;
import net.tridentsdk.event.entity.EntityDamageEvent;
import net.tridentsdk.server.entity.TridentLivingEntity;
import net.tridentsdk.window.trade.Trade;

import java.util.Collection;
import java.util.UUID;

/**
 * Represents a Villager
 *
 * @author The TridentSDK Team
 */
public class TridentVillager extends TridentLivingEntity implements Villager {
    private volatile VillagerCareer career;
    private volatile VillagerProfession role;

    public TridentVillager(UUID uuid, Position spawnPosition, VillagerCareer career, VillagerProfession role) {
        super(uuid, spawnPosition);
        this.career = career;
        this.role = role;
    }

    @Override
    public VillagerProfession profession() {
        return role;
    }

    @Override
    public void setProfession(VillagerProfession profession) {
        this.role = profession;
    }

    @Override
    public VillagerCareer career() {
        return career;
    }

    @Override
    public void setCareer(VillagerCareer career) {
        this.career = career;
    }

    @Override
    public int careerLevel() {
        return 0;
    }

    @Override
    public int age() {
        return 0;
    }

    @Override
    public void setAge(int ticks) {

    }

    @Override
    public boolean canBreed() {
        return false;
    }

    @Override
    public boolean isInLove() {
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
    public Collection<Trade> trades() {
        return null;
    }

    @Override
    public EntityType type() {
        return EntityType.VILLAGER;
    }
}
