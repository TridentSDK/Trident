/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
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
import io.netty.handler.codec.ReplayingDecoder;
import net.tridentsdk.logger.Logger;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.packet.Packet;
import net.tridentsdk.server.packet.PacketIn;
import net.tridentsdk.server.packet.PacketRegistry;
import net.tridentsdk.server.packet.play.PlayOutChat;
import net.tridentsdk.ui.chat.ChatComponent;
import net.tridentsdk.ui.chat.ChatType;

import javax.annotation.concurrent.ThreadSafe;
import java.math.BigInteger;
import java.util.List;
import java.util.zip.Inflater;

import static net.tridentsdk.server.net.NetData.arr;
import static net.tridentsdk.server.net.NetData.rvint;

/**
 * This is the first decoder in the pipeline. Incoming
 * packets are read and decompressed through this decoder.
 */
@ThreadSafe
public class InDecoder extends ReplayingDecoder<Void> {
    /**
     * The logger used for debugging packets
     */
    private static final Logger LOGGER = Logger.get(InDecoder.class);

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
        // Step 1: Decrypt if enabled
        // If not, use the raw buffer
        ByteBuf decrypt = buf;
        NetCrypto crypto = this.client.getCryptoModule();
        if (crypto != null) {
            decrypt = ctx.alloc().buffer();
            crypto.decrypt(buf, decrypt);
        }

        // Step 2: Decompress if enabled
        // If not, compressed, use raw buffer
        int fullLen = rvint(decrypt);
        ByteBuf decompressed;
        if (this.client.doCompression()) {
            int uncompressed = rvint(decrypt);
            if (uncompressed != 0) {
                if (uncompressed < TridentServer.cfg().compressionThresh()) {
                    this.client.disconnect("Incorrect compression header");
                    return;
                }

                decompressed = ctx.alloc().buffer();
                byte[] in = arr(decrypt, fullLen - BigInteger.valueOf(uncompressed).toByteArray().length);

                Inflater inflater = new Inflater();
                inflater.setInput(in);

                byte[] buffer = new byte[NetClient.BUFFER_SIZE];
                while (!inflater.finished()) {
                    int bytes = inflater.inflate(buffer);
                    decompressed.writeBytes(buffer, 0, bytes);
                }
            } else {
                decompressed = decrypt.readBytes(fullLen - OutEncoder.VINT_LEN);
            }
        } else {
            decompressed = decrypt.readBytes(fullLen);
        }

        try {
            // Step 3: Decode packet
            int id = rvint(decompressed);

            Class<? extends Packet> cls = PacketRegistry.byId(this.client.getState(), Packet.Bound.SERVER, id);
            PacketIn packet = PacketRegistry.make(cls);

            LOGGER.debug("RECV: " + packet.getClass().getSimpleName());
            packet.read(decompressed, this.client);
        } finally {
            decompressed.release();

            // If we created a new buffer, release it here
            if (decrypt != buf) {
                decrypt.release();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        NetClient client = NetClient.get(ctx);
        if (client != null) {
            client.sendPacket(new PlayOutChat(ChatComponent.create().setText("Server error: " + cause.getMessage()), ChatType.SYSTEM, true));
        } else {
            ctx.channel().close().addListener(future -> LOGGER.error(ctx.channel().remoteAddress() + " disconnected due to server error"));
        }

        throw new RuntimeException(cause);
    }
}