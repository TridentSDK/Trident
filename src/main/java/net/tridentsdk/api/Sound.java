/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
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
package net.tridentsdk.api;

/**
 * TODO more darude sandstorm's
 */
public enum Sound {
    AMBIENT_CAVE("ambient.cove.cave"),
    AMBIENT_WEATHER_RAIN("ambient.weather.rain"),
    AMBIENT_WEATHER_THUNDER("ambient.weather.thunder"),

    DAMAGE_FALLBIG("damage.fallbig"),
    DAMAGE_FALLSMALL("damage.fallsmall"),

    FIRE_ACTIVE("fire.active"),
    FIRE_IGNITE("fire.ignite"),

    LIQUID_LAVA("liquid.lava"),
    LIQUID_LAVAPOP("liquid.lavapop"),
    LIQUID_SPLASH("liquid.splash"),
    LIQUID_WATER("liquid.water"),

    MOB_BLAZE_BREATHE("mob.blaze.breathe"),
    MOB_BLAZE_DEATH("mob.blaze.death"),
    MOB_BLAZE_HIT("mob.blaze.hit"),

    MOB_CAT_HISS("mob.cat.hiss"),
    MOB_CAT_HITT("mob.cat.hitt"),
    MOB_CAT_MEOW("mob.cat.meow"),
    MOB_CAT_PURREOW("mob.cat.purreow");

    private final String s;

    Sound(String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return this.s;
    }
}
