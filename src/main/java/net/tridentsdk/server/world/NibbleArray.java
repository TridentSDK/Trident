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
package net.tridentsdk.server.world;

import java.util.Objects;

/**
 * An array of 4-bit values, called nibbles. Two nibbles are stored in one byte.
 *
 * The order in which the nibbles are stored is rather interesting: instead of
 * doing it like this:
 *
 * <pre>
 * byte nr:    --1--  --2--  --3--
 * nibble nr:  1---2  3---4  5---6
 * </pre>
 *
 * <p>
 * it is stored like this:
 *
 * <pre>
 * byte nr:    --1--  --2--  --3--
 * nibble nr:  2---1  4---3  6---5
 * </pre>
 *
 * This has been done to remain compatible with Minecraft
 */
public class NibbleArray {
    /**
     * Analogous to {@link #get(int)}, but works for byte arrays not wrapped in
     * a {@link NibbleArray} object. Benefit is that you don't have to waste
     * time creating NibbleArray instances. However, you lose proper bounds
     * checking (can get nibble at position that is one too far), proper null
     * checking (you'll want to check the byte[] array when you retrieve it, not
     * when you try to read from it) and have code that is a little uglier.
     *
     * @param bytes
     *            Bytes to read from.
     * @param position
     *            Position of the nibble to read from.
     * @return The nibble.
     * @throws ArrayIndexOutOfBoundsException
     *             If {@code position < 0 || position >= bytes.length * 2}.
     */
    public static byte getInArray(byte[] bytes, int position) throws ArrayIndexOutOfBoundsException {
        int arrayPos = position / 2;
        boolean getOnLeftFourBits = (position % 2) != 0;
        if (getOnLeftFourBits) {
            return (byte) ((bytes[arrayPos] >> 4) & 0b0000__1111);
        } else {
            return (byte) (bytes[arrayPos] & 0b0000__1111);
        }
    }

    /**
     * Analogous to {@link #set(int, byte)}, but works for byte arrays not
     * wrapped in a {@link NibbleArray} object. See the
     * {@link #getInArray(byte[], int)} method for a note about use cases for
     * this method.
     *
     * @param bytes
     *            The bytes to change.
     * @param position
     *            The nibble position.
     * @param value
     *            The nibble to set.
     * @throws IndexOutOfBoundsException
     *             If {@code position < 0 || position >= bytes.length * 2}.
     */
    public static void setInArray(byte[] bytes, int position, byte value) throws IndexOutOfBoundsException {
        int arrayPos = position / 2;
        boolean setOnLeftFourBits = (position % 2) != 0;

        byte previous = bytes[arrayPos];
        if (setOnLeftFourBits) {
            bytes[arrayPos] = (byte) ((previous & 0b0000__1111) | ((value << 4) & 0b1111__0000));
        } else {
            bytes[arrayPos] = (byte) ((previous & 0b1111__0000) | (value & 0b0000__1111));
        }
    }

    private final byte[] bytes;

    private final int length;

    /**
     * Creates a new nibble array.
     *
     * @param bytes
     *            The bytes, used as storage.
     */
    public NibbleArray(byte[] bytes) {
        this.bytes = Objects.requireNonNull(bytes);
        this.length = bytes.length * 2;
    }

    public NibbleArray(int length) {
        this.bytes = new byte[(length + 1) / 2];
        this.length = length;
    }

    /**
     * Gets the byte at the given position. Only the lowest four bits of the
     * byte will be used.
     *
     * @param position
     *            The position.
     * @return The byte.
     * @throws ArrayIndexOutOfBoundsException
     *             If {@code position < 0 || position >= length()}.
     */
    public byte get(int position) throws ArrayIndexOutOfBoundsException {
        // Check for negative positions, because (int) (-1 / 2) == 0, so it
        // would otherwise validate silently
        if (position < 0 || position >= length) {
            throw new ArrayIndexOutOfBoundsException("Position is " + position + ", array size is " + length);
        }

        return getInArray(bytes, position);
    }

    /**
     * Gets the underlying byte array.
     *
     * @return The underlying byte array.
     */
    public byte[] getHandle() {
        return bytes;
    }

    /**
     * Gets how many 4-bit values will fit into this array.
     *
     * @return The length.
     */
    public int length() {
        return length;
    }

    /**
     * Sets the byte at the given index to the contents of the given byte. The
     * highest four bits of the byte will silently be discarded.
     *
     * @param position
     *            The nibble position.
     * @param value
     *            The new value.
     * @throws ArrayIndexOutOfBoundsException
     *             If {@code position < 0 || position > length()}.
     */
    public void set(int position, byte value) throws ArrayIndexOutOfBoundsException {
        // Check for negative positions, because (int) (-1 / 2) == 0, so it
        // would otherwise validate silently
        if (position < 0 || position >= length) {
            throw new ArrayIndexOutOfBoundsException("Position is " + position + ", array size is " + length);
        }

        setInArray(bytes, position, value);
    }
}
