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
import io.netty.handler.codec.MessageToByteEncoder;
import net.tridentsdk.command.logger.Logger;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.packet.PacketOut;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
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
     * The deflater used for compressing packets
     */
    private static final ThreadLocal<Deflater> DEFLATER = new ThreadLocal<Deflater>() {
        @Override
        protected Deflater initialValue() {
            return new Deflater(Deflater.BEST_SPEED);
        }
    };
    /**
     * Length of an uncompressed packet using the
     * compressed transport.
     */
    private static final int VINT_LEN = BigInteger.ZERO.toByteArray().length;
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
    protected void encode(ChannelHandlerContext ctx, PacketOut msg, ByteBuf out) throws Exception {
        // Step 1: Encode packet
        ByteBuf payload = ctx.alloc().buffer();
        wvint(payload, msg.id());
        msg.write(payload);

        // Step 2: Compress if enabled
        // If not, write headers to new buffer
        ByteBuf buf = ctx.alloc().buffer();
        if (this.client.doCompression()) {
            int len = payload.readableBytes();
            if (len > COMPRESSION_THRESH) {
                this.writeDeflated(payload, buf, len);
            } else {
                this.writeCompressed(payload, buf);
            }
        } else {
            this.writeUncompressed(payload, buf);
        }

        // Step 3: Encrypt if enabled
        // If not, write raw bytes
        NetCrypto crypto = this.client.cryptoModule();
        if (crypto != null) {
            crypto.encrypt(buf, out);
        } else {
            out.writeBytes(buf);
        }

        payload.release();
        buf.release();
        LOGGER.debug("SEND: " + msg.getClass().getSimpleName());
    }

    /**
     * Writes a compressed packet that is deflated using
     * zlib.
     *
     * @param payload the payload to write
     * @param out the output buffer
     * @param len the length
     * @throws IOException if something goes wrong
     */
    private void writeDeflated(ByteBuf payload, ByteBuf out, int len) throws IOException {
        payload.markReaderIndex();
        byte[] input = arr(payload, len);

        Deflater deflater = DEFLATER.get();
        deflater.setInput(input);
        deflater.finish();

        byte[] buffer = new byte[NetClient.BUFFER_SIZE];
        ByteBuf result = payload.alloc().buffer();
        while (!deflater.finished()) {
            int deflated = deflater.deflate(buffer);
            result.writeBytes(buffer, 0, deflated);
        }

        deflater.reset();

        int resultLen = result.readableBytes();
        if (resultLen >= len) {
            // if no compression happened, write the same
            // uncompressed payload
            payload.resetReaderIndex();
            this.writeCompressed(payload, out);
        } else {
            wvint(out, resultLen + BigInteger.valueOf(len).toByteArray().length);
            wvint(out, len);
            out.writeBytes(result);
        }

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
}