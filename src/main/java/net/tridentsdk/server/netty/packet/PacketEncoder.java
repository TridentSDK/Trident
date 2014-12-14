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
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.util.TridentLogger;

import java.math.BigInteger;
import java.util.zip.Deflater;

/**
 * @author The TridentSDK Team
 */
public class PacketEncoder extends MessageToByteEncoder<ByteBuf> {

    private final Deflater deflater = new Deflater();
    private final byte[] buffer = new byte[1024];
    private ClientConnection connection;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        connection = ClientConnection.getConnection(ctx);
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf msg, ByteBuf out) throws Exception {
        boolean underThreshold = msg.readableBytes() < TridentServer.getInstance().getCompressionThreshold();

        if(underThreshold && connection.isCompressionEnabled()) {
            sendDecompressed(msg, out);
        } else if(!(underThreshold) && connection.isCompressionEnabled()) {
            sendCompressed(msg, out);
        } else {
            Codec.writeVarInt32(out, msg.readableBytes());
            out.writeBytes(msg);
        }
    }

    private void sendDecompressed(ByteBuf msg, ByteBuf out) {
        Codec.writeVarInt32(out, msg.readableBytes() + BigInteger.valueOf(0).toByteArray().length);
        Codec.writeVarInt32(out, 0);
        out.writeBytes(msg);
    }

    private void sendCompressed(ByteBuf msg, ByteBuf out) {
        int index = msg.readerIndex();
        int length = msg.readableBytes();

        byte[] decompressed = new byte[length];

        msg.readBytes(decompressed);
        deflater.setInput(decompressed);
        deflater.finish();

        ByteBuf compressed = Unpooled.buffer();
        int compressedLength = 0;
        int readLength;

        while((readLength = deflater.deflate(buffer)) > 0) {
            compressedLength += readLength;
            compressed.writeBytes(buffer, 0, readLength);
        }

        deflater.end();
        deflater.reset();

        TridentLogger.log("Compressed: " + compressedLength + " decompressed: " + length);

        if(compressedLength == 0 || compressedLength > length) {
            msg.readerIndex(index);
            sendDecompressed(msg, out);
            return;
        }

        Codec.writeVarInt32(out, compressedLength + BigInteger.valueOf(length).toByteArray().length);
        Codec.writeVarInt32(out, length);
        out.writeBytes(compressed);
    }
}
