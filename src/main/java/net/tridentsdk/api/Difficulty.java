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
 * Minecraft difficulty for the players
 * <p/>
 * <p>If you need more help, take a look at <a href="http://minecraft.gamepedia.com/Difficulty">Minecraft Wiki</a>.</p>
 *
 * @author The TridentSDK Team
 */
public enum Difficulty {
    PEACEFUL(0),
    EASY(1),
    NORMAL(2),
    HARD(3);

    private final byte b;

    Difficulty(int i) {
        this.b = (byte) i;
    }

    /**
     * Returns the {@code byte} value of the Difficulty
     *
     * @return {@code byte} value of the Difficulty
     */
    public byte toByte() {
        return this.b;
    }

    public static Difficulty getDifficulty(int i) {
        for (Difficulty difficulty : values()) {
            if (difficulty.b == i) {
                return difficulty;
            }
        }

        return Difficulty.NORMAL;
    }
}
