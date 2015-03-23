package net.tridentsdk.server.entity;

import net.tridentsdk.Position;
import net.tridentsdk.entity.ExperienceOrb;
import net.tridentsdk.entity.types.EntityType;

import java.util.UUID;

/**
 * Represents an orb of experience
 *
 * @author The TridentSDK Team
 */
public class TridentExpOrb extends TridentEntity implements ExperienceOrb {
    public TridentExpOrb(UUID uuid, Position spawnPosition) {
        super(uuid, spawnPosition);
    }

    @Override
    public int age() {
        return 0;
    }

    @Override
    public void setAge(int age) {

    }

    @Override
    public short health() {
        return 0;
    }

    @Override
    public void setHealth(short health) {

    }

    @Override
    public EntityType type() {
        return EntityType.EXPERIENCE_ORB;
    }
}
