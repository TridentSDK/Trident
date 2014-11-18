/*
 *     TridentSDK - A Minecraft Server API
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
