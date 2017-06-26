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

package net.tridentsdk.server.netty.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;

/**
 * Used to compress (if needed) outgoing packets from the server
 * <p>Note that this is not thread safe, if it is to be used in multiple threads, multiple instances should be
 * created</p>
 * <p>This is the second and final in the outbound packet pipeline</p>
 *
 * @author The TridentSDK Team
 */
@NotThreadSafe
public class PacketEncoder extends MessageToByteEncoder<ByteBuf> {
    private final Deflater deflater = new Deflater(Deflater.BEST_SPEED);
    private final byte[] buffer = new byte[65536];
    private final ByteArrayOutputStream compressed = new ByteArrayOutputStream();

    private ClientConnection connection;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        connection = ClientConnection.connection(ctx);
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf msg, ByteBuf out) throws Exception {
        int threshold = TridentServer.instance().compressionThreshold();
        boolean underThreshold = msg.readableBytes() < threshold && threshold != -1;

        if (underThreshold && connection.isCompressionEnabled()) {
            sendDecompressed(msg, out);
        } else if (!(underThreshold) && connection.isCompressionEnabled()) {
            sendCompressed(msg, out);
        } else {
            Codec.writeVarInt32(out, msg.readableBytes());
            out.writeBytes(msg);
        }
    }

    /**
     * Encodes the packet without checking for size to see if it should be compressed
     * <p>Still sends a VarInt 0 to indicate that this packet has not been compressed</p>
     * <p>This method of handling a packet is abnormal and is only used when compression is disabled</p>
     * FIXME compression needs to be enabled in order to use this method
     */
    private void sendDecompressed(ByteBuf msg, ByteBuf out) {
        Codec.writeVarInt32(out, msg.readableBytes() + Codec.sizeOf(0));
        Codec.writeVarInt32(out, 0);
        out.writeBytes(msg);
    }

    /**
     * Checks a packets size and encodes (writes the size, compressed size, and data) and compressed the information if
     * necessary
     */
    private void sendCompressed(ByteBuf msg, ByteBuf out) throws IOException {
        int index = msg.readerIndex();
        int length = msg.readableBytes();

        byte[] decompressed = new byte[length];

        msg.readBytes(decompressed);
        deflater.setInput(decompressed);
        deflater.finish();

        while (!deflater.finished()) {
            int bytes = deflater.deflate(buffer);
            compressed.write(buffer, 0, bytes);
        }

        int afterCompress = compressed.toByteArray().length;

        // Equals or more than original size
        if (afterCompress == length || afterCompress > length) {
            msg.readerIndex(index);
            sendDecompressed(msg, out);
            return;
        }

        deflater.reset();

        Codec.writeVarInt32(out, afterCompress + Codec.sizeOf(length));
        Codec.writeVarInt32(out, length);
        out.writeBytes(compressed.toByteArray());

        compressed.reset();
    }
}
