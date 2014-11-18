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
package net.tridentsdk.entity;

/**
 * Possible rabbit types, color and friendliness
 */
public enum RabbitType {
    BROWN(0),

    WHITE(1),

    BLACK(2),

    WHITE_AND_BLACK(3),

    GOLD(4),

    SALT_AND_PEPPER(5),

    KILLER_RABBIT(99);

    private static final RabbitType[] byId = new RabbitType[7];

    static {
        for (RabbitType type : RabbitType.values()) {
            byId[type.id] = type;
            // TODO by ordinal?
        }
    }

    private final int id;

    RabbitType(int id) {
        this.id = id;
    }

    /**
     * Returns the {@code int} value of the RabbitType
     *
     * @return {@code int} value of the RabbitType
     */
    public int toInt() {
        return this.id;
    }

    /**
     * Returns the {@code int} value of the RabbitType
     *
     * @param rabbitType RabbitType
     * @return {@code int} value of the RabbitType
     */
    public static int toInt(RabbitType rabbitType) {
        return rabbitType.toInt();
    }
}
