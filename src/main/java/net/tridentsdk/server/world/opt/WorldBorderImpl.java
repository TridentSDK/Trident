package net.tridentsdk.server.world.opt;

import lombok.RequiredArgsConstructor;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.world.IntPair;
import net.tridentsdk.world.opt.WorldBorder;

/**
 * The implementation of a world border which can be enabled
 * on a world.
 */
@RequiredArgsConstructor
public class WorldBorderImpl implements WorldBorder {
    /**
     * The world which contains this world border
     */
    private final TridentWorld world;

    @Override
    public IntPair getCenter() {
        return null;
    }

    @Override
    public void setCenter(IntPair center) {

    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public void setSize(int size, int time) {

    }

    @Override
    public void grow(int delta, int time) {

    }

    @Override
    public double getDamage() {
        return 0;
    }

    @Override
    public void setDamage(double damage) {

    }

    @Override
    public int getSafeZoneDistance() {
        return 0;
    }

    @Override
    public void setSafeZoneDistance(int size) {

    }

    @Override
    public int getWarnDistance() {
        return 0;
    }

    @Override
    public void setWarnDistance(int dist) {

    }

    @Override
    public int getWarnTime() {
        return 0;
    }

    @Override
    public void setWarnTime(int time) {

    }
}