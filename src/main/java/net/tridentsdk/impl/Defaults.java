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
package net.tridentsdk.impl;

// if these shouldn't exist, or should go somewhere else, just move them
// this is probably temporary

import net.tridentsdk.api.Difficulty;

/**
 * Contains the default values used in impl.json
 *
 * @author The TridentSDK Team
 */
public final class Defaults {
    /**
     * Maximum allowed players on the impl
     */
    public static final int MAX_PLAYERS = 10;
    /**
     * The text displayed below the impl name in the multiplayer menu
     */
    public static final String MOTD = "Just another Trident server...";
    /**
     * The difficulty of the game
     */
    public static final Difficulty DIFFICULTY = Difficulty.EASY;
    /**
     * The icon on the left of the impl
     */
    public static final String MOTD_IMAGE_LOCATION = "/impl-icon.png";
    /**
     * Scheduler mode
     */
    public static final boolean IN_A_HURRY_MODE = true;

    public static final int PORT = 25565;
    public static final String ADDRESS = "localhost";

    private Defaults() {
    }
}
