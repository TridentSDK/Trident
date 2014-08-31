/*
 * Copyright (C) 2014 The TridentSDK Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tridentsdk.server.netty.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import net.tridentsdk.api.Trident;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.protocol.Protocol;

import java.util.List;

/**
 * Channel handler that decodes the packet data sent from the stream in the form of the byte buffer. This is needed to
 * interpret the data sent correctly, and make sure that the data maintains its transmission integrity. <p/> <p>Note
 * this is not shareable. It must be thread confined, or create a new instance for each channel.</p>
 *
 * @author The TridentSDK Team
 */
public class PacketDecoder extends ReplayingDecoder<PacketDecoder.State> {
    private final Protocol protocol;
    private int length;

    /**
     * Creates the decoder and initializes the state
     */
    public PacketDecoder() {
        super(PacketDecoder.State.LENGTH);
        this.protocol = ((TridentServer) Trident.getServer()).getProtocol();
    }

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf buf, List<Object> objects) throws Exception {
        switch (this.state()) {
            case LENGTH:
                this.length = Codec.readVarInt32(buf);
                this.checkpoint(PacketDecoder.State.DATA);

            case DATA:
                //Makes sure that there are enough bytes for the whole packet
                buf.markReaderIndex();
                buf.readBytes(this.length);
                buf.resetReaderIndex();

                //Gets the packet id from the data
                int id = Codec.readVarInt32(buf);

                //Copies the Buf's data to put into a PacketData instance
                ByteBuf dataCopy = Unpooled.copiedBuffer(buf);
                dataCopy.readerIndex(buf.readerIndex());
                //Moves the readerIndex of the input buf to the end, to signify that we've read the packet
                buf.skipBytes(this.length);

                //Passes the PacketData instance to be processed downstream
                objects.add(new PacketData(id, dataCopy));

                this.checkpoint(PacketDecoder.State.LENGTH);
        }
    }

    /**
     * The current read state of the decoder
     *
     * @author The TridentSDK Team
     */
    enum State {
        LENGTH, DATA
    }
}
