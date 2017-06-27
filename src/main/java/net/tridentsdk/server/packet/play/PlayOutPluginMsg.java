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
package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;

import static net.tridentsdk.server.net.NetData.NET_CHARSET;
import static net.tridentsdk.server.net.NetData.wstr;

/**
 * Plugin message packet, used to send the brand after the
 * {@link PlayOutJoinGame} packet has been sent.
 */
@Immutable
public final class PlayOutPluginMsg extends PacketOut {
    /**
     * The branding packet
     */
    public static final PlayOutPluginMsg BRAND =
            new PlayOutPluginMsg("MC|Brand", "tridentsdk".getBytes(NET_CHARSET));
    /**
     * The channel name
     */
    private final String channel;
    /**
     * The message payload
     */
    private final byte[] data;

    public PlayOutPluginMsg(String channel, byte[] data) {
        super(PlayOutPluginMsg.class);
        if (data.length >= Short.MAX_VALUE) {
            throw new ArrayIndexOutOfBoundsException("Data must have len < Short.MAX_VALUE");
        }

        this.channel = channel;
        this.data = data;
    }

    @Override
    public void write(ByteBuf buf) {
        wstr(buf, this.channel);
        buf.writeBytes(this.data);
    }
}
