package net.tridentsdk.server.world.opt;

import lombok.RequiredArgsConstructor;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.world.opt.Weather;

/**
 * The implementation of the current weather conditions in
 * a world.
 */
@RequiredArgsConstructor
public class WeatherImpl implements Weather {
    /**
     * The world that has these weather conditions
     */
    private final TridentWorld world;

    @Override
    public boolean isRaining() {
        return false;
    }

    @Override
    public void setRaining(boolean raining) {

    }

    @Override
    public int getRainTime() {
        return 0;
    }

    @Override
    public void setRainTime(int ticks) {

    }

    @Override
    public boolean isThundering() {
        return false;
    }

    @Override
    public void setThundering(boolean thundering) {

    }

    @Override
    public int getThunderTime() {
        return 0;
    }

    @Override
    public void setThunderTime(int ticks) {

    }

    @Override
    public void setClear() {

    }

    @Override
    public int getClearTime() {
        return 0;
    }

    @Override
    public void setClearTime(int ticks) {

    }
}
