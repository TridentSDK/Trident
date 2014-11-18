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
package net.tridentsdk.util;

import org.apache.commons.lang.Validate;

import java.util.Arrays;

public final class NibbleArray {

    private final byte[] data;

    public NibbleArray(int size) {
        this(new byte[size / 2]);
    }

    public NibbleArray(byte... data) {
        Validate.isTrue(((data.length % 2) == 0), "Size must be even! Size is " + data.length);
        this.data = data;
    }

    public int length() {
        return data.length * 2;
    }

    public int byteLength() {
        return data.length;
    }

    public byte get(int index) {
        byte b = data[index / 2];

        if ((index & 1) == 0) {
            return (byte) (b & 0x0f);
        }

        return (byte) ((b & 0xf0) >> 4);
    }

    public void set(int index, byte value) {
        value &= 0xf;

        int half = index / 2;
        byte prev = data[half];

        if ((index & 1) == 0) {
            data[half] = (byte) ((prev & 0xf0) | value);
            return;
        }

        data[half] = (byte) ((prev & 0x0f) | value);
    }

    public void fill(byte value) {
        value &= 0xf;
        Arrays.fill(data, (byte) ((value << 4) | value));
    }

    public void setRaw(byte[] source) {
        Validate.isTrue(data.length == source.length, "Byte array length must be the same as current size!");
        System.arraycopy(source, 0, data, 0, source.length);
    }
}
