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
