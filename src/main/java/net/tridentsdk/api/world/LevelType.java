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
package net.tridentsdk.api.world;

public enum LevelType {

    DEFAULT("default"),
    FLAT("flat"),
    LARGE_BIOMES("largeBiomes"), // why lowerCamelCase I'll never know
    AMPLIFIED("amplified"),
    DEFAULT_1_1("default_1_1"); // I don't even...

    private final String s;

    LevelType(String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return this.s;
    }

    public static LevelType getLevelType(String s) {
        for (LevelType level : values()) {
            if (level.s.equalsIgnoreCase(s)) {
                return level;
            }
        }

        return LevelType.DEFAULT;
    }
}
