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
package net.tridentsdk.world;

public enum Dimension {

    NETHER(-1),
    OVERWORLD(0),
    END(1);

    private final byte b;

    Dimension(int i) {
        this.b = (byte) i;
    }

    public byte toByte() {
        return this.b;
    }

    public static Dimension getDimension(int i) {
        for (Dimension dimension : values()) {
            if (dimension.b == i) {
                return dimension;
            }
        }

        return null;
    }
}
