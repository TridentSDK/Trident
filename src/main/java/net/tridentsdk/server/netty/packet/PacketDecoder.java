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
import io.netty.handler.codec.ReplayingDecoder;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Channel handler that decodes the packet data sent from the stream in the form of the byte buffer. This is needed to
 * interpret the data sent correctly, and make sure that the data maintains its transmission integrity. <p/> <p>Note
 * this is not shareable. It must be thread confined, or create a new instance for each channel.</p>
 *
 * @author The TridentSDK Team
 */
public class PacketDecoder extends ReplayingDecoder<Void> {

    private final Inflater inflater = new Inflater();
    private ClientConnection connection;
    private int rawLength;

    @Override
    public void handlerAdded(ChannelHandlerContext context) {
        this.connection = ClientConnection.getConnection(context);
    }

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf buf, List<Object> objects) throws Exception {
        boolean compressed = connection.isCompressionEnabled();

        if(compressed)
            Codec.readVarInt32(buf); // read the length of the actual data and raw length, not used

        this.rawLength = Codec.readVarInt32(buf);

        if(!(compressed) || rawLength < TridentServer.getInstance().getCompressionThreshold()) {
            ByteBuf data = buf.readBytes(this.rawLength);

            objects.add(new PacketData(data));
            return;
        }

        byte[] compressedData = new byte[buf.readableBytes()];
        byte[] decompressed = new byte[rawLength];

        buf.readBytes(compressedData);
        inflater.setInput(compressedData);

        inflater.inflate(decompressed);
        objects.add(new PacketData(Unpooled.wrappedBuffer(decompressed)));

        inflater.reset();
    }
}
