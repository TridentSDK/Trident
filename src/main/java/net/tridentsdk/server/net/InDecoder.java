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
import io.netty.handler.codec.ByteToMessageDecoder;
import net.tridentsdk.logger.Logger;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.packet.Packet;
import net.tridentsdk.server.packet.PacketIn;
import net.tridentsdk.server.packet.PacketRegistry;
import net.tridentsdk.server.player.TridentPlayer;

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
public class InDecoder extends ByteToMessageDecoder {
    /**
     * The logger used for debugging packets
     */
    private static final Logger LOGGER = Logger.get(InDecoder.class);

    /**
     * The instance of the inflater to use to decompress
     * packets
     */
    private final Inflater inflater = new Inflater();
    /**
     * The last reader index used by the decoder by the
     * decrypter on the last iteration
     */
    private ByteBuf lastDecrypted;
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
        // Original index used if the buffer needs to wait
        // for more input
        int resetIdx = buf.readerIndex();

        if (this.lastDecrypted == null) {
            this.lastDecrypted = ctx.alloc().buffer();
        }

        // Decryption begins in buf at wherever the decoder
        // left off when it last wrote to lastDecrypted
        buf.readerIndex(this.lastDecrypted.writerIndex());

        // Step 1: Decrypt if enabled
        // If not, write the bytes to the lastDecrypted buf
        int readableCrypted = buf.readableBytes();
        if (readableCrypted > 0) {
            NetCrypto crypto = this.client.getCryptoModule();
            if (crypto != null) {
                crypto.decrypt(buf, this.lastDecrypted, readableCrypted);
            } else {
                this.lastDecrypted.writeBytes(buf);
            }
        }

        // Step 2: Decompress if enabled
        // If not, compressed, use raw buffer
        // VarInt code pasted here in order to ensure that
        // there is enough bytes in the buffer to read the
        // full VarInt
        int numRead = 0;
        int fullLen = 0;
        byte read;
        do {
            if (this.lastDecrypted.readableBytes() == 0) {
                buf.readerIndex(resetIdx);
                this.lastDecrypted.readerIndex(resetIdx);
                return;
            }

            read = this.lastDecrypted.readByte();
            int value = read & 0x7f;
            fullLen |= value << 7 * numRead;

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0x80) != 0);

        if (fullLen > this.lastDecrypted.readableBytes()) {
            buf.readerIndex(resetIdx);
            this.lastDecrypted.readerIndex(resetIdx);
            return;
        }

        ByteBuf decompressed;
        if (this.client.doCompression()) { // compression enabled
            int uncompressed = rvint(this.lastDecrypted);
            if (uncompressed != 0) {
                if (uncompressed < TridentServer.cfg().compressionThresh()) {
                    this.client.disconnect("Incorrect compression header");
                    return;
                }

                decompressed = ctx.alloc().buffer();
                byte[] in = arr(this.lastDecrypted, fullLen - BigInteger.valueOf(uncompressed).toByteArray().length);

                this.inflater.setInput(in);

                byte[] buffer = new byte[NetClient.BUFFER_SIZE];
                while (!this.inflater.finished()) {
                    int bytes = this.inflater.inflate(buffer);
                    decompressed.writeBytes(buffer, 0, bytes);
                }
                this.inflater.reset();
            } else { // compression enabled, < compress thresh
                decompressed = this.lastDecrypted.readBytes(fullLen - OutEncoder.VINT_LEN);
            }
        } else { // not compressed
            decompressed = this.lastDecrypted.readBytes(fullLen);
        }

        try {
            // Step 3: Decode packet
            int id = rvint(decompressed);

            Class<? extends Packet> cls = PacketRegistry.byId(this.client.getState(), Packet.Bound.SERVER, id);
            if (cls == null) {
                String stringId = String.format("%2s", Integer.toHexString(id).toUpperCase()).replace(' ', '0');
                TridentPlayer player = this.client.getPlayer();
                if (player != null) {
                    player.sendMessage("Packet " + stringId + " => SERVER is not supported at this time");
                }
                LOGGER.warn("Client @ " + ctx.channel().remoteAddress() + " sent unsupported packet " + stringId);
            }

            PacketIn packet = PacketRegistry.make(cls);

            LOGGER.debug("RECV: " + packet.getClass().getSimpleName());
            packet.read(decompressed, this.client);
        } finally {
            decompressed.release();

            if (this.lastDecrypted.readableBytes() == 0) {
                this.lastDecrypted.release();
                this.lastDecrypted = null;
            } else {
                buf.readerIndex(this.lastDecrypted.readerIndex());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (this.client != null) {
            this.client.disconnect("Server error: " + cause.getMessage());
        } else {
            ctx.channel().close().addListener(future -> LOGGER.error(ctx.channel().remoteAddress() + " disconnected due to server error"));
        }

        throw new RuntimeException(cause);
    }
}