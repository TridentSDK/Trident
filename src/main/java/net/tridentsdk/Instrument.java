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
 * Represents the instruments that can be played on a note block
 *
 * @author The TridentSDK Team
 */
public enum Instrument {
    /**
     * Piano note
     */
    PIANO(0x0),
    /**
     * Bass drum note
     */
    BASS_DRUM(0x1),
    /**
     * Snare drum note
     */
    SNARE_DRUM(0x2),
    /**
     * Stick note
     */
    STICKS(0x3),
    /**
     * Bass guitar note
     */
    BASS_GUITAR(0x4);

    final byte id;

    Instrument(int i) {
        this.id = (byte) i;
    }

    /**
     * Resolves the Instrument from its respective Byte value
     *
     * @param b Byte representing the Instrument
     * @return Instrument from the supplied Byte
     */
    public static Instrument fromByte(byte b) {
        switch ((int) b) {
            case 0x0:
                return PIANO;
            case 0x1:
                return BASS_DRUM;
            case 0x2:
                return SNARE_DRUM;
            case 0x3:
                return STICKS;
            case 0x4:
                return BASS_GUITAR;
            default:
                return null;
        }
    }

    /**
     * Returns the {@code byte} value of an Instrument
     *
     * @param instrument Instrument
     * @return Byte value of the instrument
     */
    public static byte toByte(Instrument instrument) {
        return instrument.id;
    }

    /**
     * Returns the {@code byte} value of the Instrument
     *
     * @return Byte value of the Instrument
     */
    public byte toByte() {
        return this.id;
    }
}
