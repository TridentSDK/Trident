package net.tridentsdk.server.entity.decorate;

import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.LivingEntity;
import net.tridentsdk.entity.decorate.LivingDecorationAdapter;
import net.tridentsdk.entity.decorate.Tameable;
import net.tridentsdk.entity.living.Player;

import java.util.UUID;

public class DecoratedTameable extends LivingDecorationAdapter implements Tameable {
    private final DecoratedAgeable functionalDelegate;
    private boolean tamed;
    private boolean sitting;
    private Player tamer;

    protected DecoratedTameable(LivingEntity entity, boolean canBreed, boolean isInLove) {
        super(entity);
        functionalDelegate = Decorator.asAgeable(entity, canBreed, isInLove);
    }

    @Override
    public boolean isTamed() {
        return tamed;
    }

    @Override
    public UUID getOwner() {
        return tamer.getUniqueId();
    }

    @Override
    public boolean isSitting() {
        return sitting;
    }

    @Override
    public int getAge() {
        return functionalDelegate.getAge();
    }

    @Override
    public void setAge(int ticks) {
        functionalDelegate.setAge(ticks);
    }

    @Override
    public boolean canBreed() {
        return functionalDelegate.canBreed();
    }

    @Override
    public boolean isInLove() {
        return functionalDelegate.isInLove();
    }

    public void updateTamed(Player player) {
        if (player == null) {
            this.tamer = null;
            this.tamed = false;
            // remove pathfinding
        }
        this.tamer = player;
        this.tamed = true;
        // TODO follow
    }

    public void updateSitting(boolean sitting) {
        if (this.sitting = sitting) {
            // sit
        } else {

        }
    }

    public void applyLove(Entity other) {
        this.functionalDelegate.applyLove(other);
    }

    public void applyBreed() {
        this.functionalDelegate.applyBreed();
    }
}
