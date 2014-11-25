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
package net.tridentsdk.server.netty.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;

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

        deflater.reset();

        System.out.println("Compressed: " + compressedLength + " decompressed: " + length);

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
