package net.tridentsdk.server.util;

public class BitUtils {

    private int countSetBits(long number, int bitCount){
        int setBits = 0;

        for (int i = 0; i < 8; i++) {
            setBits += (number & 1 << i);
        }

        return setBits;
    }

    public int countSetBits(byte b){
        return countSetBits(b, 8);
    }

    public int countSetBits(short s){
        return countSetBits(s, 16);
    }

    public int countSetBits(int i){
        return countSetBits(i, 32);
    }

    public int countSetBits(long l){
        return countSetBits(l, 64);
    }

    public int countUnsetBits(byte b){
        return 8 - countSetBits(b, 8);
    }

    public int countUnsetBits(short s){
        return 16 - countSetBits(s, 16);
    }

    public int countUnsetBits(int i){
        return 32 - countSetBits(i, 32);
    }

    public int countUnsetBits(long l){
        return 64 - countSetBits(l, 64);
    }

    public byte flip(byte b){
        return (byte) ~b;
    }

    public short flip(short s){
        return (short) ~s;
    }

    public int flip(int i){
        return ~i;
    }

    public long flip(long l){
        return ~l;
    }

    public byte setBit(byte b, int bit, boolean state){
        return (byte) (state ? (b | 1 << bit) : (b & ~(1 << bit)));
    }

    public short setBit(short s, int bit, boolean state){
        return (short) (state ? (s | 1 << bit) : (s & ~(1 << bit)));
    }

    public int setBit(int i, int bit, boolean state){
        return (int) (state ? (i | 1 << bit) : (i & ~(1 << bit)));
    }

    public long setBit(long l, int bit, boolean state){
        return (long) (state ? (l | 1 << bit) : (l & ~(1 << bit)));
    }

}
