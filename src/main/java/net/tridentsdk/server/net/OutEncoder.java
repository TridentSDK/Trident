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

import java.io.IOException;
import java.math.BigInteger;
import java.util.zip.Deflater;

/**
 * The encoder which writes packet messages to the stream.
 */
// TODO use one instance only
public class OutEncoder extends MessageToByteEncoder<PacketOut> {
    /**
     * The logger used for debugging packets
     */
    private static final Logger LOGGER = Logger.get(OutEncoder.class);
    /**
     * Length of an uncompressed packet using the
     * compressed
     * transport.
     */
    private static final int VINT_LEN = BigInteger.valueOf(0).toByteArray().length;
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
        ByteBuf payloadBuf = ctx.alloc().buffer();
        NetPayload payload = new NetPayload(payloadBuf);

        // Write packet ID + data
        // -> payload
        payload.writeVInt(msg.id());
        msg.write(payload);

        ByteBuf buf = ctx.alloc().buffer();
        // -> buf
        if (this.client.doCompression()) {
            int len = buf.readableBytes();
            if (len > COMPRESSION_THRESH) {
                this.writeDeflated(buf, len, payloadBuf);
            } else {
                this.writeCompressed(buf, payloadBuf);
            }
        } else {
            this.writeUncompressed(buf, payloadBuf);
        }

        // -> out
        NetCrypto crypto = this.client.cryptoModule();
        if (crypto != null) {
            crypto.encrypt(buf, out);
        } else {
            out.writeBytes(buf);
        }

        LOGGER.debug("SEND: " + msg.getClass().getSimpleName());
    }

    /**
     * Writes a compressed packet that is deflated using
     * zlib.
     *
     * @param out the output buffer
     * @param len the length
     * @param payload the payload to write
     * @throws IOException if something goes wrong
     */
    private void writeDeflated(ByteBuf out, int len, ByteBuf payload) throws IOException {
        byte[] bytes = new byte[len];
        payload.readBytes(bytes);

        Deflater deflater = new Deflater(Deflater.BEST_SPEED);
        deflater.setInput(bytes);
        deflater.finish();

        byte[] buffer = new byte[8192];
        while (deflater.deflate(buffer) > -1) {
            payload.writeBytes(buffer);
        }

        deflater.end();
        int resultLen = payload.readableBytes();

        if (resultLen >= len) {
            this.writeCompressed(out, payload);
        } else {
            NetPayload.writeVInt(out, VINT_LEN + BigInteger.valueOf(resultLen).toByteArray().length);
            NetPayload.writeVInt(out, resultLen);
            out.writeBytes(payload);
        }
    }

    /**
     * Writes an uncompressed packet using the compressed
     * protocol format.
     *
     * @param out the output buffer
     * @param payload the payload to write
     */
    private void writeCompressed(ByteBuf out, ByteBuf payload) {
        NetPayload.writeVInt(out, VINT_LEN + payload.readableBytes());
        NetPayload.writeVInt(out, 0);
        out.writeBytes(payload);
    }

    /**
     * Writes an uncompressed packet.
     *
     * @param out the output buffer
     * @param payload the payload to write
     */
    private void writeUncompressed(ByteBuf out, ByteBuf payload) {
        NetPayload.writeVInt(out, payload.readableBytes());
        out.writeBytes(payload);
    }
}