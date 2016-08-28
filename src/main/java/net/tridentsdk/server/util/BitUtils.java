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

import javax.annotation.concurrent.Immutable;

/**
 * Utilities for dealing with bit operations.
 */
@Immutable
public final class BitUtils {
    // Prevent instantiation
    private BitUtils() {
    }

    /**
     * Counts the bits set in the given value.
     *
     * @param number the value to count set bits
     * @param bitCount unused
     * @return the amount of bits set in the given value
     */
    private static int countSetBits(long number, int bitCount) {
        int setBits = 0;

        for (int i = 0; i < 8; i++) {
            setBits += (number & 1 << i);
        }

        return setBits;
    }

    /**
     * Counts the bits set to 1 in the given byte.
     *
     * @param b the value to count bits
     * @return the amount of set bits
     */
    public static int countSetBits(byte b) {
        return countSetBits(b, 8);
    }

    /**
     * Counts the bits set to 1 in the given short.
     *
     * @param s the value to count bits
     * @return the amount of set bits
     */
    public static int countSetBits(short s) {
        return countSetBits(s, 16);
    }

    /**
     * Counts the bits set to 1 in the given integer.
     *
     * @param i the value to count bits
     * @return the amount of set bits
     */
    public int countSetBits(int i){
        return countSetBits(i, 32);
    }

    /**
     * Counts the bits set to 1 in the given long.
     *
     * @param l the value to count bits
     * @return the amount of set bits
     */
    public int countSetBits(long l){
        return countSetBits(l, 64);
    }

    /**
     * Counts the amount of bits that have not been set in
     * the given value.
     *
     * @param b the value to count unset bits
     * @return the amount of unset bits
     */
    public int countUnsetBits(byte b){
        return 8 - countSetBits(b, 8);
    }

    /**
     * Counts the amount of bits that have not been set in
     * the given value.
     *
     * @param s the value to count unset bits
     * @return the amount of unset bits
     */
    public int countUnsetBits(short s){
        return 16 - countSetBits(s, 16);
    }

    /**
     * Counts the amount of bits that have not been set in
     * the given value.
     *
     * @param i the value to count unset bits
     * @return the amount of unset bits
     */
    public int countUnsetBits(int i){
        return 32 - countSetBits(i, 32);
    }

    /**
     * Counts the amount of bits that have not been set in
     * the given value.
     *
     * @param l the value to count unset bits
     * @return the amount of set bits
     */
    public int countUnsetBits(long l){
        return 64 - countSetBits(l, 64);
    }

    /**
     * Sets the bit in the given value to the given state.
     *
     * @param b the value to set
     * @param bit the bit to set
     * @param state the state to set at the bit in the
     * value
     * @return the value after setting the bit
     */
    public byte setBit(byte b, int bit, boolean state){
        return (byte) (state ? (b | 1 << bit) : (b & ~(1 << bit)));
    }

    /**
     * Sets the bit in the given value to the given state.
     *
     * @param s the value to set
     * @param bit the bit to set
     * @param state the state to set at the bit in the value
     * @return the value after setting the bit
     */
    public short setBit(short s, int bit, boolean state){
        return (short) (state ? (s | 1 << bit) : (s & ~(1 << bit)));
    }

    /**
     * Sets the bit in the given value to the given state.
     *
     * @param i the value to set
     * @param bit the bit to set
     * @param state the state to set at the bit in the value
     * @return the value after setting the bit
     */
    public int setBit(int i, int bit, boolean state) {
        return state ? (i | 1 << bit) : (i & ~(1 << bit));
    }

    /**
     * Sets the bit in the given value to the given state.
     *
     * @param l the value to set
     * @param bit the bit to set
     * @param state the state to set at the bit in the value
     * @return the value after setting the bit
     */
    public long setBit(long l, int bit, boolean state) {
        return state ? (l | 1 << bit) : (l & ~(1 << bit));
    }
}
