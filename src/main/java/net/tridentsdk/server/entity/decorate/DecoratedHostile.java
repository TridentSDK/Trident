package net.tridentsdk.server.entity.decorate;

import net.tridentsdk.entity.LivingEntity;
import net.tridentsdk.entity.decorate.Hostile;
import net.tridentsdk.entity.decorate.LivingDecorationAdapter;

public class DecoratedHostile extends LivingDecorationAdapter implements Hostile {
    protected DecoratedHostile(LivingEntity entity) {
        super(entity);
    }

    public void applyHostilityUpdate() {
        // TODO pathfind
    }
}
