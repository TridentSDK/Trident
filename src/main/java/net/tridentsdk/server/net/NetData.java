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
package net.tridentsdk.server.net;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.base.AbstractVector;
import net.tridentsdk.base.Vector;

import javax.annotation.concurrent.Immutable;
import java.nio.charset.Charset;

/**
 * This class contains procedural-style constructs for
 * writing and reading data from ByteBufs provided by netty
 * in the format specified by the Minecraft protocol.
 *
 * <p>This is done mainly for performance reasons as using
 * a stream-style wrapper over ByteBuf in order to provide
 * only a few constructs is a waste of memory and precious
 * time in the handlers.</p>
 */
@Immutable
public final class NetData {
    /**
     * The default character encoding for protocol Strings.
     */
    public static final Charset NET_CHARSET = Charset.forName("UTF-8");

    // Prevent instantiation
    private NetData() {
    }

    /**
     * Transfers all of the next readable bytes from the
     * buffer into a new byte array.
     *
     * @param buf the buffer to transfer from
     * @return the an array containing the bytes of the
     * buffer
     */
    public static byte[] arr(ByteBuf buf) {
        return arr(buf, buf.readableBytes());
    }

    /**
     * Transfers the specified length of bytes from the
     * buffer into a new byte array.
     *
     * @param buf the buffer to transfer from
     * @param len the length of bytes to transfer
     * @return the new byte array
     */
    public static byte[] arr(ByteBuf buf, int len) {
        byte[] bytes = new byte[len];
        buf.readBytes(bytes, 0, len);

        return bytes;
    }

    /**
     * Reads the next String value from the byte stream
     * represented by the given buffer.
     *
     * @param buf the buffer which to read the String
     * @return the next String value
     */
    public static String rstr(ByteBuf buf) {
        // String prefixed by VINT length
        // Followed by byte array of char contents
        int len = rvint(buf);
        byte[] stringData = arr(buf, len);

        return new String(stringData, NET_CHARSET);
    }

    /**
     * Encodes the given String into the given buffer using
     * the Minecraft protocol format.
     *
     * @param buf the buffer which to write
     * @param s the String to write
     */
    public static void wstr(ByteBuf buf, String s) {
        wvint(buf, s.length());
        buf.writeBytes(s.getBytes(NET_CHARSET));
    }

    /**
     * Reads the next VarInt from the given buffer.
     *
     * @param buf the buffer to read
     * @return the next VarInt
     */
    public static int rvint(ByteBuf buf) {
        int result = 0;
        int indent = 0;

        int b = (int) buf.readByte();
        while ((b & 0x80) == 0x80) {
            if(!(indent < 21)){
                throw new RuntimeException("Too many bytes for a VarInt32.");
            }
            
            result += (b & 0x7f) << indent;
            indent += 7;

            b = (int) buf.readByte();
        }

        result += (b & 0x7f) << indent;
        return result;
    }

    /**
     * Writes a VarInt value to the given buffer.
     *
     * @param buf the buffer to write
     * @param i the VarInt to write
     */
    public static void wvint(ByteBuf buf, int i) {
        while ((i & 0xFFFFFF80) != 0L) {
            buf.writeByte(i & 0x7F | 0x80);
            i >>>= 7;
        }

        buf.writeByte(i & 0x7F);
    }

    /**
     * Converts the given angle into protocol format.
     *
     * @param angle the angle to convert
     * @return the protocol format
     */
    public static byte convertAngle(float angle) {
        return (byte) (angle / 1.40625);
    }

    /**
     * Reads the next VarLong value from the byte stream
     * represented by the given buffer.
     *
     * @param buf the buffer which to read the VarLong
     * @return the next VarLong value
     */
    public static long rvlong(ByteBuf buf) {
        long result = 0L;
        int indent = 0;

        long b = (long) buf.readByte();
        while ((b & 0x80L) == 0x80) {
            if(!(indent < 48)){
                throw new RuntimeException("Too many bytes for a VarInt64.");
            }

            result += (b & 0x7fL) << indent;
            indent += 7;

            b = (long) buf.readByte();
        }

        result += b & 0x7fL;
        return result << indent;
    }

    /**
     * Writes a VarLong value to the given buffer.
     *
     * @param buf the buffer which to write
     * @param l the VarLong value
     */
    public static void wvlong(ByteBuf buf, long l) {
        while ((l & 0xFFFFFFFFFFFFFF80L) != 0L) {
            buf.writeByte((int) (l & 0x7FL | 0x80L));
            l >>>= 7L;
        }

        buf.writeByte((int) (l & 0x7FL));
    }

    /**
     * Reads the next vector and sets the values of it into
     * the given Vector.
     *
     * @param buf the buffer to read from
     * @param vec the vector to write to
     */
    public static void rvec(ByteBuf buf, AbstractVector<?> vec) {
        long l = buf.readLong();
        vec.set(l >> 38, l >> 26 & 0xFFF, l << 38 >> 38);
    }

    /**
     * Writes a vector value to the given buffer.
     *
     * @param buf the buffer to write
     * @param vec the Vector coordinates to write
     */
    public static void wvec(ByteBuf buf, AbstractVector<?> vec) {
        long l = ((long) vec.getIntX() & 0x3FFFFFFL) << 38 | ((long) vec.getIntY() & 0xFFFL) << 26 | (long) vec.getIntZ() & 0x3FFFFFFL;
        buf.writeLong(l);
    }
    
    /**
     * Reads the next vector and creates a new
     * Vector instance
     *
     * @param buf the buffer to read from
     * @return a new vector
     */
    public static Vector rvec(ByteBuf buf){
        long pos = buf.readLong();
        return new Vector(pos >> 38, pos >> 26 & 0xFFF, pos << 38 >> 38);
    }
}