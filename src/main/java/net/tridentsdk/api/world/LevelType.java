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
