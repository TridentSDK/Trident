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
import io.netty.handler.codec.MessageToByteEncoder;
import net.tridentsdk.logger.Logger;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.packet.PacketOut;

import javax.annotation.concurrent.ThreadSafe;
import java.math.BigInteger;
import java.util.zip.Deflater;

import static net.tridentsdk.server.net.NetData.arr;
import static net.tridentsdk.server.net.NetData.wvint;

/**
 * The encoder which writes packet messages to the stream.
 */
@ThreadSafe
public class OutEncoder extends MessageToByteEncoder<PacketOut> {
    /**
     * The logger used for debugging packets
     */
    private static final Logger LOGGER = Logger.get(OutEncoder.class);
    /**
     * Length of an uncompressed packet using the
     * compressed transport.
     */
    public static final int VINT_LEN = BigInteger.ZERO.toByteArray().length;

    /**
     * The deflater used for compressing packets
     */
    private final Deflater deflater = new Deflater(Deflater.BEST_SPEED);
    /**
     * The net client which holds this channel handler
     */
    private NetClient client;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.client = NetClient.get(ctx);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, PacketOut msg, ByteBuf out) throws Exception {
        // Step 1: Encode packet
        ByteBuf payload = ctx.alloc().buffer();
        try {
            wvint(payload, msg.id());
            msg.write(payload);

            // Step 2: Compress if enabled
            // If not, write headers to new buffer
            ByteBuf buf = ctx.alloc().buffer();
            try {
                if (this.client.doCompression()) {
                    int len = payload.readableBytes();
                    if (len > TridentServer.cfg().compressionThresh()) {
                        this.writeDeflated(payload, buf, len);
                    } else {
                        this.writeCompressed(payload, buf);
                    }
                } else {
                    this.writeUncompressed(payload, buf);
                }

                // Step 3: Encrypt if enabled
                // If not, write raw bytes
                NetCrypto crypto = this.client.getCryptoModule();
                if (crypto != null) {
                    crypto.encrypt(buf, out);
                } else {
                    out.writeBytes(buf);
                }
            } finally {
                buf.release();
            }
        } finally {
            payload.release();
        }
        LOGGER.debug("SEND: " + msg.getClass().getSimpleName());
    }

    /**
     * Writes a compressed packet that is deflated using
     * zlib.
     *
     * @param payload the payload to write
     * @param out the output buffer
     * @param len the length
     */
    private void writeDeflated(ByteBuf payload, ByteBuf out, int len) {
        byte[] input = arr(payload, len);

        this.deflater.setInput(input);
        this.deflater.finish();

        byte[] buffer = new byte[NetClient.BUFFER_SIZE];
        ByteBuf result = payload.alloc().buffer();
        while (!this.deflater.finished()) {
            int deflated = this.deflater.deflate(buffer);
            result.writeBytes(buffer, 0, deflated);
        }

        this.deflater.reset();

        int resultLen = result.readableBytes();
        wvint(out, resultLen + BigInteger.valueOf(len).toByteArray().length);
        wvint(out, len);
        out.writeBytes(result);

        result.release();
    }

    /**
     * Writes an uncompressed packet using the compressed
     * protocol format.
     *
     * @param payload the payload to write
     * @param out the output buffer
     */
    private void writeCompressed(ByteBuf payload, ByteBuf out) {
        wvint(out, VINT_LEN + payload.readableBytes());
        wvint(out, 0);
        out.writeBytes(payload);
    }

    /**
     * Writes an uncompressed packet.
     *
     * @param payload the payload to write
     * @param out the output buffer
     */
    private void writeUncompressed(ByteBuf payload, ByteBuf out) {
        wvint(out, payload.readableBytes());
        out.writeBytes(payload);
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