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
package net.tridentsdk;

/**
 * The 16 different orientations available in MineCraft, plus up and down
 *
 * @author The TridentSDK Team
 */
public enum Orientation {
    NORTH,
    SOUTH,
    EAST,
    WEST,

    NORTH_WEST,
    SOUTH_WEST,
    NORTH_EAST,
    SOUTH_EAST,

    NORTH_NORTH_WEST,
    NORTH_WEST_WEST,
    SOUTH_SOUTH_WEST,
    SOUTH_WEST_WEST,

    NORTH_NORTH_EAST,
    NORTH_EAST_EAST,
    SOUTH_SOUTH_EAST,
    SOUTH_EAST_EAST
}
