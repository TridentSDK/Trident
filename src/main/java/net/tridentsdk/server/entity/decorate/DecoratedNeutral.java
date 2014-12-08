package net.tridentsdk.server.entity.decorate;

import net.tridentsdk.entity.LivingEntity;
import net.tridentsdk.entity.decorate.LivingDecorationAdapter;
import net.tridentsdk.entity.decorate.Neutral;

public class DecoratedNeutral extends LivingDecorationAdapter implements Neutral {
    private boolean hostility;

    protected DecoratedNeutral(LivingEntity entity) {
        super(entity);
    }

    @Override
    public boolean isHostile() {
        return hostility;
    }

    public void applyHostilityUpdate(boolean hostility) {
        this.hostility = hostility;
        // TODO
    }
}
