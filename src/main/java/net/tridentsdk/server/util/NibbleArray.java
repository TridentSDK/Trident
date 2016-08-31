/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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
package net.tridentsdk.server.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NibbleArray {

    @NonNull
    private final byte[] bytes;

    public NibbleArray(int length) {
        this.bytes = new byte[length];
    }

    public int getLength() {
        return bytes.length * 2;
    }

    public byte getByte(int position) {
        checkPosition(position);

        if((position % 2) == 0) {
            return (byte) (bytes[position / 2] & 0x0F);
        } else {
            return (byte) ((bytes[position / 2] >> 4) & 0x0F);
        }
    }

    public void setByte(int position, byte value) {
        checkPosition(position);

        int arrayPos = position / 2;
        if((position % 2) == 0) {
            bytes[arrayPos] = (byte) ((bytes[arrayPos] & 0xF0) | (value & 0x0F));
        } else {
            bytes[arrayPos] = (byte) ((bytes[arrayPos] & 0x0F) | ((value << 4) & 0xF0));
        }
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void fill(byte value) {
        value = (byte) (value << 4 | value);

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = value;
        }
    }

    private void checkPosition(int position) {
        if(position < 0 || position >= getLength()) {
            throw new IndexOutOfBoundsException("Index: " + position + ", Size: " + getLength());
        }
    }

}
