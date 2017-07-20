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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.tridentsdk.meta.nbt.Tag;
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

    @Getter
    private final IntPair center = DEFAULT_CENTER;
    @Getter
    private final int size = DEFAULT_SIZE;

    @Override
    public void setCenter(IntPair center) {

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

    public void read(Tag.Compound compound) {
    }

    public void write(Tag.Compound compound) {
    }
}