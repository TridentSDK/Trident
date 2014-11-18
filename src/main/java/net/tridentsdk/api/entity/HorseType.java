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
package net.tridentsdk.api.entity;

public enum HorseType {
    /**
     * Generic Horse
     */
    HORSE(0),

    /**
     * Donkey
     */
    DONKEY(1),

    /**
     * Mule
     */
    MULE(2),

    /**
     * Zombie horse
     */
    ZOMBIE(3),

    /**
     * Skeleton horse
     */
    SKELETON(4);
    private static final HorseType[] byId = new HorseType[5];

    static {
        for (HorseType type : HorseType.values()) {
            byId[type.id] = type;
        }
    }

    private final int id;

    HorseType(int id) {
        this.id = id;
    }

}
