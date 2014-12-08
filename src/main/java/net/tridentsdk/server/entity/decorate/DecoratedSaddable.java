package net.tridentsdk.server.entity.decorate;

import net.tridentsdk.entity.LivingEntity;
import net.tridentsdk.entity.decorate.LivingDecorationAdapter;
import net.tridentsdk.entity.decorate.Saddleable;

public class DecoratedSaddable extends LivingDecorationAdapter implements Saddleable {
    private boolean saddled;

    protected DecoratedSaddable(LivingEntity entity) {
        super(entity);
    }

    @Override
    public boolean isSaddled() {
        return saddled;
    }

    @Override
    public void setSaddled(boolean saddled) {
        this.saddled = saddled;
    }

    // rider can be null if only the saddle should be applied
    public void applySaddle(LivingEntity rider) {
        // TODO
    }
}
