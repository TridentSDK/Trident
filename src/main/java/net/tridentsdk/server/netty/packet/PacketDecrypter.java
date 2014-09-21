/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *     3. Neither the name of TridentSDK nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.server.netty.packet;

import java.util.List;

import net.tridentsdk.server.netty.ClientConnection;
import net.tridentsdk.server.netty.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * Decrypts incoming packets and forwards the bytes to the PacketDecoder
 * 
 * @author The TridentSDK Team
 */
public class PacketDecrypter extends ByteToMessageDecoder {
    private ClientConnection connection;
    
    @Override
    public void handlerAdded(ChannelHandlerContext context) {
        connection = ClientConnection.getConnection(context);
    }
    
    /* (non-Javadoc)
     * @see io.netty.handler.codec.ByteToMessageDecoder#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in,
            List<Object> out) throws Exception {
        ByteBuf bufOut = ctx.alloc().buffer(in.readableBytes());
        if (connection.isEncryptionEnabled()) {
            bufOut.writeBytes(connection.decrypt(Codec.toArray(in)));
        } else {
            bufOut.writeBytes(in);
        }
        out.add(bufOut);

    }

}
