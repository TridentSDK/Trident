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

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.zip.Deflater;

/**
 * @author The TridentSDK Team
 */
public class PacketEncoder extends MessageToByteEncoder<ByteBuf> {
    //private final Deflater deflater = new Deflater(Deflater.BEST_SPEED);
    private ClientConnection connection;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        connection = ClientConnection.getConnection(ctx);
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf msg, ByteBuf out) throws Exception {
        int threshold = TridentServer.getInstance().getCompressionThreshold();
        boolean underThreshold = msg.readableBytes() < threshold && threshold != -1;

        if (underThreshold && connection.isCompressionEnabled()) {
            sendDecompressed(msg, out);
        } else if (!(underThreshold) && connection.isCompressionEnabled()) {
            sendCompressed(msg, out);
        } else {
            Codec.writeVarInt32(out, msg.readableBytes());
            out.writeBytes(msg);
        }

        Files.write(Paths.get("lastpacket.txt"), Arrays.asList(DatatypeConverter.printHexBinary(Codec.asArray(out.copy()))),
                Charset.defaultCharset());
    }

    private void sendDecompressed(ByteBuf msg, ByteBuf out) {
        Codec.writeVarInt32(out, msg.readableBytes() + Codec.sizeOf(0));
        Codec.writeVarInt32(out, 0);
        out.writeBytes(msg);
    }

    private void sendCompressed(ByteBuf msg, ByteBuf out) throws IOException {
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        int index = msg.readerIndex();
        int length = msg.readableBytes();

        byte[] buffer = new byte[1024];
        byte[] decompressed = new byte[length];

        msg.readBytes(decompressed);
        deflater.setInput(decompressed);
        deflater.finish();

        ByteArrayOutputStream compressed = new ByteArrayOutputStream();
        //DeflaterOutputStream stream = new DeflaterOutputStream(compressed, deflater);

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

        deflater.end();

        Codec.writeVarInt32(out, afterCompress + Codec.sizeOf(length));
        Codec.writeVarInt32(out, length);
        out.writeBytes(compressed.toByteArray());
    }
}
