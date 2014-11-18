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
package net.tridentsdk.board;

/**
 * The type of board, also where it is displayed
 *
 * @author The TridentSDK Team
 */
public enum BoardType {
    LIST(0),
    SIDEBAR(1),
    BELOW_NAME(2);

    private final byte position;

    BoardType(int position) {
        this.position = (byte) position;
    }

    /**
     * Returns the {@code byte} value of the BoardType
     *
     * @return {@code byte} value of the BoardType
     */
    public byte toByte() {
        return this.position;
    }

    /**
     * Returns the {@code byte} value of the BoardType
     *
     * @param boardType BoardType
     * @return {@code byte} value of the BoardType
     */
    public static byte toByte(BoardType boardType) {
        return boardType.toByte();
    }
}
