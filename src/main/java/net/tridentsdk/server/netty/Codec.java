/*
 *     Trident - A Multithreaded Server Alternative
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
package net.tridentsdk.server.netty;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;

import javax.annotation.concurrent.ThreadSafe;
import java.nio.charset.Charset;

/**
 * Utility class to help decode the bytes from a backed buffer serializer
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public final class Codec {
    //Current charset used by strings is UTF_8
    /**
     * The charset used for Strings
     */
    public static final Charset CHARSET = Charsets.UTF_8;

    private Codec() {
    } // Suppress initialization of utility class

    /**
     * Read a string from the encoded buffer
     *
     * @param buf the buffer to decode the string from
     * @return the decoded string read from the buffer
     */
    public static String readString(ByteBuf buf) {
        //Reads the length of the string
        int length = readVarInt32(buf);
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);

        return new String(bytes, CHARSET);
    }

    /**
     * Writes a string to the buffer
     *
     * @param buf the buffer to decode the string from
     */
    public static void writeString(ByteBuf buf, String string) {
        //Writes the length of the string
        writeVarInt32(buf, string.length());

        //Writes the bytes of the string
        byte[] bytes = string.getBytes(CHARSET);
        buf.writeBytes(bytes);
    }

    /**
     * Reads a 32bit VarInt from the encoded buffer
     *
     * @param buf the buffer to decode the integer from
     * @return the decoded integer read from the buffer
     */
    public static int readVarInt32(ByteBuf buf) {
        //The result we will return
        int result = 0;

        //How much to indent the current bytes
        int indent = 0;
        int b = (int) buf.readByte();

        //If below, it means there are more bytes
        // 0x80 = 128 for those that don't know
        while ((b & 0x80) == 0x80) {
            Preconditions.checkArgument(indent < 21, "Too many bytes for a VarInt32.");

            //Adds the byte in the appropriate position (first byte goes last, etc.)
            result += (b & 0x7f) << indent;
            indent += 7;

            //Reads the next byte
            b = (int) buf.readByte();
        }

        // 0x7f = 127
        return result += (b & 0x7f) << indent;
    }

    /**
     * Writes an int value as a VarInt to the buffer.
     *
     * @param buf      the buffer to encode into
     * @param toEncode the integer encode into buf
     */
    public static void writeVarInt32(ByteBuf buf, int toEncode) {
        //Loops through until the currently 'selected' set of 7 bits is the terminating one
        while ((toEncode & 0xFFFFFF80) != 0L) {
            /*Writes the selected 7 bits, and adds a 1 at the front
            signifying that there is another byte*/
            buf.writeByte(toEncode & 0x7F | 0x80);
            //Selects the next set of 7 bits
            toEncode >>>= 7;
        }
        //Writes the final terminating byte with a 0 at the front to signify termination
        buf.writeByte(toEncode & 0x7F);
    }

    /**
     * Reads a 64bit VarInt from the encoded buffer
     *
     * @param buf the buffer to decode the long from
     * @return the decoded long read from the buffer
     */
    public static long readVarInt64(ByteBuf buf) {
        //The result we will return
        long result = 0L;

        //How much to indent the current bytes
        int indent = 0;
        long b = (long) buf.readByte();

        //If below, it means there are more bytes
        while ((b & 0x80L) == 0x80) {
            Preconditions.checkArgument(indent < 49, "Too many bytes for a VarInt64.");

            //Adds the byte in the appropriate position (first byte goes last, etc.)
            result += (b & 0x7fL) << indent;
            indent += 7;

            //Reads the next byte
            b = (long) buf.readByte();
        }

        return result += (b & 0x7fL) << indent;
    }

    /**
     * Writes a long value as a VarInt to the buffer.
     *
     * @param buf      the buffer to encode into
     * @param toEncode the integer encode into buf
     */
    public static void writeVarInt64(ByteBuf buf, long toEncode) {
        //Loops through until the currently 'selected' set of 7 bits is the terminating one
        while ((toEncode & 0xFFFFFFFFFFFFFF80L) != 0L) {
            /*Writes the selected 7 bits, and adds a 1 at the front
            signifying that there is another byte*/
            buf.writeByte((int) (toEncode & 0x7FL | 0x80L));
            //Selects the next set of 7 bits
            toEncode >>>= 7L;
        }
        //Writes the final terminating byte with a 0 at the front to signify termination
        buf.writeByte((int) (toEncode & 0x7FL));
    }

    /**
     * Writes the full contents of a ByteBuf to an array
     *
     * @param buf the buffer to get data from
     * @return bytes the array of bytes
     */
    public static byte[] toArray(ByteBuf buf) {
        return toArray(buf, buf.readableBytes());
    }

    /**
     * Writes a certain length of bytes from a ByteBuf to an array
     *
     * @param buf    the buffer to get data from
     * @param length the length to toPacket
     * @return bytes the array of bytes
     */
    public static byte[] toArray(ByteBuf buf, int length) {
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        return bytes;
    }
}
