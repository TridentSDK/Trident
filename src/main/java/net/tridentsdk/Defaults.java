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
package net.tridentsdk;

// if these shouldn't exist, or should go somewhere else, just move them
// this is probably temporary

import net.tridentsdk.api.Difficulty;

/**
 * Contains the default values used in server.json
 *
 * @author The TridentSDK Team
 */
public final class Defaults {
    /**
     * Maximum allowed players on the server
     */
    public static final int MAX_PLAYERS = 10;
    /**
     * The text displayed below the server name in the multiplayer menu
     */
    public static final String MOTD = "Just another Trident server...";
    /**
     * The difficulty of the game
     */
    public static final Difficulty DIFFICULTY = Difficulty.EASY;
    /**
     * The icon on the left of the server
     */
    public static final String MOTD_IMAGE_LOCATION = "/server-icon.png";
    /**
     * Scheduler mode
     */
    public static final boolean IN_A_HURRY_MODE = true;

    public static final int PORT = 25565;
    public static final String ADDRESS = "localhost";

    private Defaults() {
    }
}
