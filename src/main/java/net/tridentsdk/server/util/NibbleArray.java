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
     * Obtains the number of nibbles multiplied by two.
     *
     * @return the length of the nibbles * 2
     */
    public int getLength() {
        return (this.nibbles.length() * BYTES_PER_LONG) << 1;
    }

    /**
     * Obtains the byte at the given position index in the
     * nibble array.
     *
     * @param position the nibble index
     * @return the nibble value at the index
     */
    public byte getByte(int position) {
        this.checkPosition(position);

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
        long shift = (nibblePosition % BYTES_PER_LONG) << 3;
        long shifted = splice >> shift;
        byte b = (byte) (shifted & 0xFF);

        if ((position & 1) == 0) {
            return (byte) (b & 0x0F);
        } else {
            return (byte) ((b >> 4) & 0x0F);
        }
    }

    /**
     * Sets the nibble at the given position index to the
     * given nibble value.
     *
     * @param position the nibble index
     * @param value the nibble value
     */
    public void setByte(int position, byte value) {
        this.checkPosition(position);

        // Code seems messy because the constant values are
        // hoisted to prevent recalculating every iteration
        // Same as get byte; however we need additional bit
        // logic in order to prevent bitwise OR from
        // combining the bits from the old long value
        // clearShift is the left bits needed to clear the
        // topmost bytes in order to isolate the lower part
        // of the splice
        // Likewise, the upperShift is the bits shifted
        // right plus the amount of bits in a byte (8) that
        // is needed to isolate the higher part of the
        // splice
        // Some bitshifting magic is done to obtain the new
        // splice. Basically, the old splice is shifted
        // left and back by a specific amount of bytes in
        // order to clear the top bits and the value that
        // was originally at the byte position of the
        // nibble. Then, the shifted value is shifted right
        // and back to clear the old byte value. The result
        // of OR between these two values is the top and
        // bottom portions of the splice being set, leaving
        // the middle 8 bits (those which are required by
        // the new byte) unset. This prevents OR conflicts
        // from existing set bits that are being unset.

        int nibblePosition = position / 2;
        int spliceIndex = nibblePosition / BYTES_PER_LONG;
        int shift = (nibblePosition % BYTES_PER_LONG) << 3;
        int clearShift = 64 - shift;
        int upperShift = shift + 8;

        long oldSpice; // easter egg (play Old Spice theme)
        long newSplice;
        if ((position & 1) == 0) {
            int nibble = value & 0x0F;
            do {
                oldSpice = this.nibbles.get(spliceIndex);
                long shifted = oldSpice >> shift;
                byte oldByte = (byte) (shifted & 0xFF);
                byte newByte = (byte) (oldByte & 0xF0 | nibble);

                newSplice = oldSpice << clearShift >> clearShift | shifted >> 8 << upperShift | (long) newByte << shift;
            }
            while (!this.nibbles.compareAndSet(spliceIndex, oldSpice, newSplice));
        } else {
            int nibble = value << 4 & 0xF0;
            do {
                oldSpice = this.nibbles.get(spliceIndex);
                long shifted = oldSpice >> shift;
                byte oldB = (byte) (shifted & 0xFF);
                byte newB = (byte) (oldB & 0x0F | nibble);

                newSplice = oldSpice << clearShift >> clearShift | shifted >> 8 << upperShift | (long) newB << shift;
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
        byte newValue = (byte) (value << 4 | value);
        for (int i = 0; i < 64; i += 8) {
            splice |= (long) newValue << i;
        }

        for (int i = 0; i < this.nibbles.length(); i++) {
            this.nibbles.set(i, splice);
        }
    }

    /**
     * Range check.
     */
    private void checkPosition(int position) {
        if (position < 0 || position >= this.getLength()) {
            throw new IndexOutOfBoundsException("Index: " + position + ", Size: " + this.getLength());
        }
    }
}