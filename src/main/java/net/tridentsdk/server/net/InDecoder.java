/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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
package net.tridentsdk.server.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.tridentsdk.command.logger.Logger;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.packet.Packet;
import net.tridentsdk.server.packet.PacketIn;
import net.tridentsdk.server.packet.PacketRegistry;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.zip.Inflater;

/**
 * This is the first decoder in the pipeline. Incoming
 * packets are read and decompressed through this decoder.
 */
@ThreadSafe
public class InDecoder extends ByteToMessageDecoder {
    /**
     * The logger used for debugging packets
     */
    private static final Logger LOGGER = Logger.get(InDecoder.class);
    /**
     * Obtains the configured compression threshold.
     */
    public static final int COMPRESSION_THRESH = TridentServer.cfg().compressionThresh();

    /**
     * The net client which holds this channel handler
     */
    private NetClient client;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.client = NetClient.get(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.client.setState(NetClient.NetState.HANDSHAKE);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> list) throws Exception {
        NetCrypto crypto = this.client.cryptoModule();
        NetPayload payload;
        if (crypto != null) {
            ByteBuf decrypt = ctx.alloc().buffer();
            crypto.decrypt(buf, decrypt);
            payload = new NetPayload(decrypt);
        } else {
            payload = new NetPayload(buf);
        }

        if (this.client.doCompression()) {
            payload.readVInt(); // toss
            int compressedLen = payload.readVInt();
            if (buf.readableBytes() > COMPRESSION_THRESH) {
                byte[] in = payload.readBytes(compressedLen);

                Inflater inflater = new Inflater();
                inflater.setInput(in);

                byte[] buffer = new byte[8192];
                while (inflater.inflate(buffer) > -1) {
                    payload.writeBytes(buffer);
                }
            } else {
                payload.writeBytes(buf);
            }
        } else {
            payload.readVInt(); // toss
        }

        int id = payload.readVInt();

        Class<? extends Packet> cls = PacketRegistry.byId(this.client.state(), Packet.Bound.SERVER, id);
        PacketIn packet = PacketRegistry.make(cls);

        LOGGER.debug("RECV: " + packet.getClass().getSimpleName());
        packet.read(payload, this.client);
    }
}