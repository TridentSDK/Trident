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
package net.tridentsdk.api.board;

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
