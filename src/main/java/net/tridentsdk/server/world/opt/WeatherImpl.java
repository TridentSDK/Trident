/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tridentsdk.server.world.opt;

import lombok.RequiredArgsConstructor;
import net.tridentsdk.meta.nbt.Tag;
import net.tridentsdk.server.concurrent.PoolSpec;
import net.tridentsdk.server.concurrent.ServerThreadPool;
import net.tridentsdk.server.packet.play.PlayOutGameState;
import net.tridentsdk.server.packet.play.PlayOutLightning;
import net.tridentsdk.server.player.RecipientSelector;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.world.opt.Weather;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The implementation of the current weather conditions in
 * a world.
 */
@RequiredArgsConstructor
public class WeatherImpl implements Weather {
    /**
     * The max time in ticks that can elapse before the
     * weather toggles (3 hours)
     */
    private static final int MAX_RAND = 20 * 60 * 60 * 3;

    /**
     * The world that has these weather conditions
     */
    private final TridentWorld world;

    /**
     * The current weather state
     */
    private final AtomicReference<WeatherState> weatherState = new AtomicReference<>(WeatherState.CLEAR);

    /**
     * Amount of time in ticks until the rain toggles
     */
    private final AtomicInteger rainTime = new AtomicInteger(ThreadLocalRandom.current().nextInt(MAX_RAND));
    /**
     * Amount of time in ticks until thunder toggles, unless
     * it is not raining
     */
    private final AtomicInteger thunderTime = new AtomicInteger(ThreadLocalRandom.current().nextInt(MAX_RAND));

    /**
     * The state of the weather in the world.
     */
    private enum WeatherState {
        CLEAR, RAINING, RAINING_THUNDERING
    }

    @Override
    public void clear() {
        this.weatherState.set(WeatherState.CLEAR);
        RecipientSelector.inWorld(this.world, new PlayOutGameState(1, 0));
    }

    @Override
    public void beginRaining() {
        // Fails only if RAINING or RAINING_THUNDERING, in
        // which case it is already raining
        if (this.weatherState.compareAndSet(WeatherState.CLEAR, WeatherState.RAINING)) {
            RecipientSelector.inWorld(this.world, new PlayOutGameState(2, 0));
        }
    }

    @Override
    public void beginThunder() {
        // Fails only if CLEAR or RAINING_THUNDERING
        // Which means that it cannot be thundering or is
        // already thundering
        this.weatherState.compareAndSet(WeatherState.RAINING, WeatherState.RAINING_THUNDERING);
    }

    @Override
    public void stopThunder() {
        // Fails if CLEAR or RAINING, which means that it
        // isn't thundering already
        this.weatherState.compareAndSet(WeatherState.RAINING_THUNDERING, WeatherState.RAINING);
    }

    @Override
    public boolean isRaining() {
        WeatherState state = this.weatherState.get();
        return state == WeatherState.RAINING || state == WeatherState.RAINING_THUNDERING;
    }

    @Override
    public int getRainTime() {
        return this.rainTime.get();
    }

    @Override
    public void setRainTime(int ticks) {
        if (ticks == RANDOM_TIME) {
            ticks = ThreadLocalRandom.current().nextInt(MAX_RAND);
        }
        this.rainTime.set(ticks);
    }

    @Override
    public boolean isThundering() {
        return this.weatherState.get() == WeatherState.RAINING_THUNDERING;
    }

    @Override
    public int getThunderTime() {
        return this.thunderTime.get();
    }

    @Override
    public void setThunderTime(int ticks) {
        if (ticks == RANDOM_TIME) {
            ticks = ThreadLocalRandom.current().nextInt(MAX_RAND);
        }
        this.thunderTime.set(ticks);
    }

    @Override
    public boolean isClear() {
        return this.weatherState.get() == WeatherState.CLEAR;
    }

    @Override
    public int getClearTime() {
        return this.rainTime.get();
    }

    @Override
    public void setClearTime(int ticks) {
        if (ticks == RANDOM_TIME) {
            ticks = ThreadLocalRandom.current().nextInt(MAX_RAND);
        }
        this.rainTime.set(ticks);
    }

    public void tick() {
        WeatherState state = this.weatherState.get();
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        if (state == WeatherState.RAINING_THUNDERING) {
            ServerThreadPool.forSpec(PoolSpec.WORLDS).execute(() -> this.world.getChunks().forEach(c -> {
                ThreadLocalRandom cur = ThreadLocalRandom.current();
                if (cur.nextInt(100_000) == 69) {
                    int randX = cur.nextInt(16);
                    int randZ = cur.nextInt(16);
                    PlayOutLightning lightning = new PlayOutLightning(c.getBlockAt(randX, c.getHighestY(randX, randZ), randZ).getPosition());
                    RecipientSelector.whoCanSee(c, null, lightning);
                }
            }));
        }

        int prevRain;
        int nextRain;
        do {
            prevRain = this.rainTime.get();

            if (prevRain == 0) {
                nextRain = rand.nextInt(MAX_RAND);
            } else {
                nextRain = prevRain - 1;
            }
        }
        while (!this.rainTime.compareAndSet(prevRain, nextRain));

        if (prevRain == 0) {
            if (state == WeatherState.RAINING || state == WeatherState.RAINING_THUNDERING) {
                this.clear();
            } else {
                this.beginRaining();
            }
        }

        int prevThunder;
        int nextThunder;
        do {
            prevThunder = this.thunderTime.get();

            if (prevThunder == 0) {
                nextThunder = rand.nextInt(MAX_RAND);
            } else {
                nextThunder = prevThunder - 1;
            }
        }
        while (!this.thunderTime.compareAndSet(prevThunder, nextThunder));

        if (prevThunder == 0) {
            if (state == WeatherState.RAINING_THUNDERING) {
                this.stopThunder();
            } else if (state == WeatherState.RAINING) {
                this.beginThunder();
            }
        }
    }

    public void read(Tag.Compound compound) {
        this.rainTime.set(compound.getInt("rainTime"));
        this.thunderTime.set(compound.getInt("thunderTime"));

        if (compound.getByte("thundering") == 1) {
            this.weatherState.set(WeatherState.RAINING_THUNDERING);
        } else {
            if (compound.getByte("raining") == 1) {
                this.weatherState.set(WeatherState.RAINING);
            }
        }
    }

    public void write(Tag.Compound compound) {
        WeatherState state = this.weatherState.get();
        int rainTime = this.rainTime.get();
        compound.putByte("raining", (byte) (state == WeatherState.RAINING || state == WeatherState.RAINING_THUNDERING ? 1 : 0));
        compound.putInt("rainTime", rainTime);
        compound.putByte("thundering", (byte) (state == WeatherState.RAINING_THUNDERING ? 1 : 0));
        compound.putInt("thunderTime", this.thunderTime.get());
        compound.putInt("clearWeatherTime", rainTime);
    }
}