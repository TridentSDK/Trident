/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
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

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.atomic.AtomicLongArray;

/**
 * This object represents a data format that stores values
 * into half-bytes called nibbles. This is used for storing
 * block lighting and sky light for chunk generation.
 */
@ThreadSafe
@RequiredArgsConstructor
public class NibbleArray {
    /**
     * 8 bytes compressed into one long to represent 16
     * nibbles
     */
    public static final int BYTES_PER_LONG = 8;

    /**
     * The array of nibbles.
     *
     * <p>Nibbles are half byte values which are compacted
     * into a single long value. 8 bytes of 8 bits each fit
     * into a 64 bit long, which composes of 16 4-bit half-
     * byte nibbles.</p>
     */
    @NonNull
    private final AtomicLongArray nibbles;

    /**
     * Creates a new nibble array with an array size of the
     * given length.
     *
     * @param length the length of the nibble array
     */
    public NibbleArray(int length) {
        this.nibbles = new AtomicLongArray(length / BYTES_PER_LONG);
    }

    /**
     * Obtains the byte nibble for the given index 0-4095.
     *
     * @param array the array to obtain the nibble
     * @param idx the index to read the nibble
     * @return the nibble
     */
    public static byte getNibble(byte[] array, int idx) {
        return (byte) ((idx & 1) == 0 ? array[idx >> 1] & 0x0F : array[idx >> 1] >> 4 & 0x0F);
    }

    /**
     * Sets the nibble at the given index 0-4095 with the
     * given nibble data.
     *
     * @param array the array to set
     * @param idx the index to set the nibble
     * @param nibble the nibble data to set
     */
    public static void setNibble(byte[] array, int idx, byte nibble) {
        int i = idx >> 1;
        if ((idx & 1) == 0) {
            array[i] = (byte) (array[i] & 0xF0 | nibble & 0x0F);
        } else {
            array[i] = (byte) (array[i] & 0x0F | nibble << 4 & 0xF0);
        }
    }

    /**
     * Obtains the number of nibbles multiplied by two.
     *
     * @return the length of the nibbles * 2
     */
    public int getLength() {
        return this.nibbles.length() * BYTES_PER_LONG << 1;
    }

    /**
     * Obtains the byte at the given position index in the
     * nibble array.
     *
     * @param position the nibble index
     * @return the nibble value at the index
     */
    public byte getByte(int position) {
        // Find nibble pos b/c 2 nibbles in single byte
        // Find splice which byte is located in
        // Find shift by figuring out what offset from the
        // lowest byte is from the position, multiply by
        // bits in each byte (2^3 == 8 bits)
        // Shift the splice and mask the byte
        // Decide whether to return the low bits or high
        // bits depending on if it is even or odd
        // respectively

        int nibblePosition = position / 2;
        long splice = this.nibbles.get(nibblePosition / BYTES_PER_LONG);
        long shift = nibblePosition % BYTES_PER_LONG << 3;
        long shifted = splice >> shift;

        if ((position & 1) == 0) {
            return (byte) (shifted & 0x0F);
        } else {
            return (byte) (shifted >> 4 & 0x0F);
        }
    }

    /**
     * Sets the nibble at the given position index to the
     * given nibble value.
     *
     * <p>Even nibbles are stored on the left, odd on the
     * right.</p>
     *
     * @param position the nibble index
     * @param value the nibble value
     */
    public void setByte(int position, byte value) {
        int nibblePosition = position / 2;
        int spliceIndex = nibblePosition >> 3;
        long shift = nibblePosition % BYTES_PER_LONG << 3;

        long oldSpice; // easter egg (play Old Spice theme)
        long newSplice;
        if ((position & 1) == 0) {
            do {
                oldSpice = this.nibbles.get(spliceIndex);
                long newByte = oldSpice >>> shift & 0xF0 | value;

                newSplice = oldSpice & ~(0xFFL << shift) | newByte << shift;
            }
            while (!this.nibbles.compareAndSet(spliceIndex, oldSpice, newSplice));
        } else {
            long shiftedVal = value << 4;
            do {
                oldSpice = this.nibbles.get(spliceIndex);
                long newByte = oldSpice >>> shift & 0x0F | shiftedVal;

                newSplice = oldSpice & ~(0xFFL << shift) | newByte << shift;
            }
            while (!this.nibbles.compareAndSet(spliceIndex, oldSpice, newSplice));
        }
    }

    /**
     * Writes the data contained in the underlying nibble
     * array to the given byte buffer.
     *
     * @param buf the buffer to write
     */
    public void write(ByteBuf buf) {
        for (int i = 0, len = this.nibbles.length(); i < len; i++) {
            long l = this.nibbles.get(i);
            for (int shift = 0; shift < 64; shift += 8) {
                long shifted = l >> shift;
                byte b = (byte) (shifted & 0xFF);
                buf.writeByte(b);
            }
        }
    }

    /**
     * Fills all nibble indices of the array with the given
     * nibble value.
     *
     * @param value the value to fill
     */
    public void fill(byte value) {
        long splice = 0;
        long newValue = value << 4 | value & 0xFF;
        for (int i = 0; i < 64; i += 8) {
            splice |= newValue << i;
        }

        for (int i = 0; i < this.nibbles.length(); i++) {
            this.nibbles.set(i, splice);
        }
    }

    /**
     * Loads the bytes from the given nibble array into the
     * long striped splice array.
     *
     * @param bytes the bytes to load
     */
    public void read(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            byte value = bytes[i];
            int spliceIndex = i >> 3;
            long shift = i % BYTES_PER_LONG << 3;

            long newSplice;
            if ((i & 1) == 0) {
                long oldSpice = this.nibbles.get(spliceIndex);
                long newByte = oldSpice >>> shift & 0xF0 | value;

                newSplice = oldSpice & ~(0xFFL << shift) | newByte << shift;
            } else {
                long oldSpice = this.nibbles.get(spliceIndex);
                long newByte = oldSpice >>> shift & 0x0F | value << 4;

                newSplice = oldSpice & ~(0xFFL << shift) | newByte << shift;
            }
            this.nibbles.set(spliceIndex, newSplice);
        }
    }

    /**
     * Writes the given byte array with the data stored in
     * this spliced nibble array.
     *
     * @return the data written from this nibble array
     */
    public byte[] write() {
        byte[] bytes = new byte[this.nibbles.length() * BYTES_PER_LONG];
        for (int i = 0, len = this.nibbles.length(); i < len; i++) {
            long l = this.nibbles.get(i);
            for (int shift = 0, offset = 0; shift < 64; shift += 8, offset++) {
                long shifted = l >> shift;
                byte b = (byte) (shifted & 0xFF);
                bytes[i * BYTES_PER_LONG + offset] = b;
            }
        }
        return bytes;
    }
}