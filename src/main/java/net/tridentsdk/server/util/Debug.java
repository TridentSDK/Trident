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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Debug helper class which contains useful debug functions
 * for different purposes of testing the server.
 *
 * @author TridentSDK
 * @since 0.5-alpha
 */
@ThreadSafe
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Debug {
    public static volatile boolean IS_DEBUGGING;

    /**
     * Checks to see if the current thread that should be
     * running according to the
     * {@link net.tridentsdk.doc.Policy} annotation is
     * actually using the plugin thread. This is a very
     * crude name check and is prone to breaking should the
     * name change, but oh well. Debugging.
     */
    public static void tryCheckThread() {
        if (IS_DEBUGGING) {
            String name = Thread.currentThread().getName();
            if (!name.equals("TRD - Plugins")) {
                throw new IllegalStateException("Wrong thread: " + name);
            }
        }
    }

    /**
     * Hexdumps the contents of the given buffer.
     *
     * @param buf the buffer to dump
     * @return the hex dump
     */
    public static String debugBuffer(ByteBuf buf) {
        return debugBuffer(buf, false);
    }

    /**
     * Dumps the contents of the given buffer, with the
     * option to convert the dump to decimal instead of
     * hex.
     *
     * @param buf the buffer to dump
     * @param decimal {@code true} for decimal dump
     * @return the content dump
     */
    public static String debugBuffer(ByteBuf buf, boolean decimal) {
        int index = buf.readerIndex();
        int readableBytes = buf.readableBytes();
        String response = buf.getClass().getSimpleName() + "(" + readableBytes + "): [";

        for (int i = 0; i < readableBytes; i++) {
            if (i > 0) {
                response += ", ";
            }

            byte b = buf.readByte();
            response += String.format("%02x", b);

            if (decimal) {
                response += "(" + (0xff & b) + ")";
            }
        }

        buf.readerIndex(index);
        return response + "]";
    }
}