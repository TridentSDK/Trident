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
package net.tridentsdk.api.util;

import com.google.common.base.Preconditions;

import java.util.Arrays;

public final class NibbleArray {

    private final byte[] data;

    public NibbleArray(int size) {
        this(new byte[size / 2]);
    }

    public NibbleArray(byte... data) {
        Preconditions.checkArgument(((data.length % 2) == 0), "Size must be even! Size is " + data.length);
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
        Preconditions.checkArgument(data.length == source.length, "Byte array length must be the same as current size!");
        System.arraycopy(source, 0, data, 0, source.length);
    }
}
