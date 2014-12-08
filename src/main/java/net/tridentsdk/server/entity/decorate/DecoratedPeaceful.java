package net.tridentsdk.server.entity.decorate;

import net.tridentsdk.entity.LivingEntity;
import net.tridentsdk.entity.decorate.LivingDecorationAdapter;
import net.tridentsdk.entity.decorate.Peaceful;

public class DecoratedPeaceful extends LivingDecorationAdapter implements Peaceful {
    protected DecoratedPeaceful(LivingEntity entity) {
        super(entity);
    }

    public void applyPeaceUpdate() {
        // TODO remove pathfinding
    }
}
