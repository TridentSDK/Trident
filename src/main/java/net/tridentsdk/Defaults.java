package net.tridentsdk;


// if these shouldn't exist, or should go somewhere else, just move them
// this is probably temporary

import net.tridentsdk.api.Difficulty;

/**
 * Contains the default values used in server.json
 */
public final class Defaults {
    public static final int MAX_PLAYERS = 10;
    public static final String MOTD = "Just another Trident server...";
    public static final Difficulty DIFFICULTY = Difficulty.EASY;
    public static final String MOTD_IMAGE_LOCATION = "/server-icon.png";
}
