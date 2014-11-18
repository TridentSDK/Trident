/*
 *     Trident - A Multithreaded Server Alternative
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
