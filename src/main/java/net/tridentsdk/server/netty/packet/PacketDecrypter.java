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
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;

import java.util.List;

/**
 * Decrypts incoming packets and forwards the bytes to the PacketDecoder
 *
 * @author The TridentSDK Team
 */
public class PacketDecrypter extends ByteToMessageDecoder {
    private ClientConnection connection;

    @Override
    public void handlerAdded(ChannelHandlerContext context) {
        this.connection = ClientConnection.getConnection(context);
    }

    /* (non-Javadoc)
     * @see io.netty.handler.codec.ByteToMessageDecoder#decode(io.netty.channel.ChannelHandlerContext,
     * io.netty.buffer.ByteBuf, java.util.List)
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in,
            List<Object> out) throws Exception {
        ByteBuf bufOut = ctx.alloc().buffer(in.readableBytes());

        if (this.connection.isEncryptionEnabled()) {
            bufOut.writeBytes(this.connection.decrypt(Codec.toArray(in)));
        } else {
            bufOut.writeBytes(in);
        }

        out.add(bufOut);
    }
}
