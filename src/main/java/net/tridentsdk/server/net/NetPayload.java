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
package net.tridentsdk.server.net;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import net.tridentsdk.base.AbstractVector;
import net.tridentsdk.base.Vector;

import java.nio.charset.Charset;

/**
 * This class represents the data that is sent in a packet
 * (that is not the ID nor the length headers) in
 * uncompressed and unencrypted form. It is essentially a
 * DataI/OStream for net data, but it modifies and adds
 * a few methods that are decoded and encoded differently
 * in the Minecraft protocol.
 */
public class NetPayload {
    /**
     * The charset that is used by the Minecraft protocol
     * to
     * write and encode strings.
     */
    public static final Charset NET_CHARSET = Charsets.UTF_8;
    /**
     * The payload.
     */
    private final ByteBuf payload;

    /**
     * Creates a new payload wrapper using the given
     * buffer.
     *
     * @param payload the payload to wrap
     */
    public NetPayload(ByteBuf payload) {
        this.payload = payload;
    }

    /**
     * Reads the bytes of the payload and transfers it into
     * the given buffer.
     *
     * @param buf the buffer into which to transfer
     */
    public void readBytes(ByteBuf buf) {
        this.payload.readBytes(buf);
    }

    /**
     * Writes the bytes in the given buffer into the
     * payload.
     *
     * @param buf the buffer to transfer from
     */
    public void writeBytes(ByteBuf buf) {
        this.payload.writeBytes(buf);
    }

    /**
     * Reads the bytes of the payload and transfers it into
     * the given payload.
     *
     * @param payload the payload to which to transfer
     */
    public void readBytes(NetPayload payload) {
        this.payload.readBytes(payload.asBuf());
    }

    /**
     * Writes the contents of the given payload into this
     * payload.
     *
     * @param payload the payload from which to transfer
     */
    public void writeBytes(NetPayload payload) {
        this.payload.writeBytes(payload.asBuf());
    }

    /**
     * Reads the bytes of the payload and transfers it into
     * the given array.
     *
     * @return the bytes that were read
     */
    public byte[] readBytes(int len) {
        byte[] bytes = new byte[len];
        this.payload.readBytes(bytes);
        return bytes;
    }

    /**
     * Writes the bytes in the given array into the
     * payload.
     *
     * @param bytes the array to transfer from
     */
    public void writeBytes(byte[] bytes) {
        this.payload.writeBytes(bytes);
    }

    /**
     * @return the next boolean
     */
    public boolean readBoolean() {
        return this.payload.readBoolean();
    }

    /**
     * Write a boolean to the payload.
     *
     * @param bool the value to write
     */
    public void writeBoolean(boolean bool) {
        this.payload.writeBoolean(bool);
    }

    /**
     * @return the next byte
     */
    public byte readByte() {
        return this.payload.readByte();
    }

    /**
     * Write a byte to the payload.
     *
     * @param b the value to write
     */
    public void writeByte(int b) {
        this.payload.writeByte(b);
    }

    /**
     * @return the next byte, unsigned
     */
    public short readUnsignedByte() {
        return this.payload.readUnsignedByte();
    }

    /**
     * Write an unsigned byte to the payload.
     *
     * @param b the value to write
     */
    public void writeUnsignedByte(int b) {
        Preconditions.checkState(b >= 0);
        this.payload.writeByte(b);
    }

    /**
     * @return the next short
     */
    public short readShort() {
        return this.payload.readShort();
    }

    /**
     * Write a short to the payload.
     *
     * @param s the value to write
     */
    public void writeShort(short s) {
        this.payload.writeShort(s);
    }

    /**
     * @return the next short, unsigned
     */
    public int readUnsignedShort() {
        return this.payload.readUnsignedShort();
    }

    /**
     * Write an unsigned short to the payload.
     *
     * @param s the value to write
     */
    public void writeUnsignedShort(short s) {
        Preconditions.checkState(s >= 0);
        this.payload.writeShort(s);
    }

    // MC protocol spec basically never uses these next 4
    // methods to transport integer and long values.
    // While these methods are used sometimes and are
    // correctly implemented, always be sure to double check
    // and ensure that the proper methods are being used
    // as they have similar names and functions.

    /**
     * @return the next integer
     * @deprecated almost never used, always double check
     */
    @Deprecated
    public int readInt() {
        return this.payload.readInt();
    }

    /**
     * Write an integer to the payload.
     *
     * @param i the value to write
     * @deprecated almost never used, always double check
     */
    @Deprecated
    public void writeInt(int i) {
        this.payload.writeInt(i);
    }

    /**
     * @return the next long
     * @deprecated almost never used, always double check
     */
    @Deprecated
    public long readLong() {
        return this.payload.readLong();
    }

    /**
     * Write a long to the payload.
     *
     * @param l the value to write
     * @deprecated almost never used, always double check
     */
    @Deprecated
    public void writeLong(long l) {
        this.payload.writeLong(l);
    }

    /**
     * @return the next float
     */
    public float readFloat() {
        return this.payload.readFloat();
    }

    /**
     * Write a float to the payload.
     *
     * @param f the value to write
     */
    public void writeFloat(float f) {
        this.payload.writeFloat(f);
    }

    /**
     * @return the next double
     */
    public double readDouble() {
        return this.payload.readDouble();
    }

    /**
     * Write a double to the payload.
     *
     * @param d the value to write
     */
    public void writeDouble(double d) {
        this.payload.writeDouble(d);
    }


    /**
     * @return the next String
     */
    public String readString() {
        // String prefixed by VINT length
        // Followed by byte array of char contents
        int len = this.readVInt();
        byte[] stringData = new byte[len];
        this.payload.readBytes(stringData);

        return new String(stringData, NET_CHARSET);
    }

    /**
     * Write a String to the payload.
     *
     * @param s the value to write
     */
    public void writeString(String s) {
        this.writeVInt(s.length());
        this.payload.writeBytes(s.getBytes(NET_CHARSET));
    }

    /**
     * @return the next VarInt
     */
    public int readVInt() {
        int result = 0;
        int indent = 0;

        int b = (int) this.readByte();
        while ((b & 0x80) == 0x80) {
            Preconditions.checkArgument(indent < 21, "Too many bytes for a VarInt32.");
            result += (b & 0x7f) << indent;
            indent += 7;

            b = (int) this.readByte();
        }

        result += (b & 0x7f) << indent;
        return result;
    }

    /**
     * Procedural style of reading the next varint.
     *
     * @param buf the buffer to read
     * @return the next varint
     */
    public static int readVInt(ByteBuf buf) {
        int result = 0;
        int indent = 0;

        int b = (int) buf.readByte();
        while ((b & 0x80) == 0x80) {
            Preconditions.checkArgument(indent < 21, "Too many bytes for a VarInt32.");
            result += (b & 0x7f) << indent;
            indent += 7;

            b = (int) buf.readByte();
        }

        result += (b & 0x7f) << indent;
        return result;
    }

    /**
     * Write a VarInt to the payload.
     *
     * @param i the value to write
     */
    public void writeVInt(int i) {
        while ((i & 0xFFFFFF80) != 0L) {
            this.writeByte(i & 0x7F | 0x80);
            i >>>= 7;
        }

        this.writeByte(i & 0x7F);
    }

    /**
     * Procedural style way of writing varint to a buffer.
     *
     * @param buf the buffer to write
     * @param i the integer to write
     */
    public static void writeVInt(ByteBuf buf, int i) {
        while ((i & 0xFFFFFF80) != 0L) {
            buf.writeByte(i & 0x7F | 0x80);
            i >>>= 7;
        }

        buf.writeByte(i & 0x7F);
    }

    /**
     * @return the next VarLong
     */
    public long readVLong() {
        long result = 0L;
        int indent = 0;

        long b = (long) this.readByte();
        while ((b & 0x80L) == 0x80) {
            Preconditions.checkArgument(indent < 49, "Too many bytes for a VarInt64.");

            result += (b & 0x7fL) << indent;
            indent += 7;

            b = (long) this.readByte();
        }

        result += (b & 0x7fL);
        return result << indent;
    }

    /**
     * Write a VarLong to the payload.
     *
     * @param l the value to write
     */
    public void writeVLong(long l) {
        while ((l & 0xFFFFFFFFFFFFFF80L) != 0L) {
            this.payload.writeByte((int) (l & 0x7FL | 0x80L));
            l >>>= 7L;
        }

        this.payload.writeByte((int) (l & 0x7FL));
    }

    /**
     * Reads the next vector and sets the values of it into
     * the one provided.
     */
    public void readVector(AbstractVector<?> vec) {
        long l = this.readLong();
        vec.set(l >> 38, (l >> 26) & 0xFFF, l << 38 >> 38);
    }

    /**
     * Write a Vector to the payload.
     *
     * @param vec the value to write
     */
    public void writeVector(AbstractVector<?> vec) {
        Vector v = new Vector();
        vec.vecWrite(v);

        long l = ((v.intX() & 0x3FFFFFF) << 38) | ((v.intY() & 0xFFF) << 26) | (v.intZ() & 0x3FFFFFF);
        this.payload.writeLong(l);
    }

    /**
     * Obtains the amount of bytes left to read after they
     * have been written.
     *
     * @return writerIndex - readerIndex
     */
    public int readableBytes() {
        return this.payload.readableBytes();
    }

    /**
     * Obtains the payload that is being written by this
     * wrapper.
     *
     * @return the raw payload bytes
     */
    public ByteBuf asBuf() {
        return this.payload;
    }
}