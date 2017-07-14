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

    public void read(Tag.Compound compound) {
    }

    public void write(Tag.Compound compound) {
    }
}