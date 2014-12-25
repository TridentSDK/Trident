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
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ByteBuf bufOut = ctx.alloc().buffer(in.readableBytes());

        if (this.connection.isEncryptionEnabled()) {
            bufOut.writeBytes(this.connection.decrypt(Codec.toArray(in)));
        } else {
            bufOut.writeBytes(in);
        }

        out.add(bufOut);
    }
}
