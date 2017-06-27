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
import net.tridentsdk.doc.Debug;
import net.tridentsdk.plugin.channel.PluginChannel;
import net.tridentsdk.plugin.channel.SimpleChannelListener;
import net.tridentsdk.server.concurrent.PoolSpec;
import net.tridentsdk.server.concurrent.ServerThreadPool;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.net.NetData;
import net.tridentsdk.server.packet.PacketIn;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.plugin.TridentPluginChannel;

import javax.annotation.concurrent.Immutable;
import java.io.ByteArrayOutputStream;

import static net.tridentsdk.server.net.NetData.rstr;

/**
 * Packet sent by the client after {@link PlayOutPlayerAbilities}
 * to confirm to the server the client brand.
 */
@Immutable
public final class PlayInPluginMsg extends PacketIn {
    public PlayInPluginMsg() {
        super(PlayInPluginMsg.class);
    }

    @Debug("Client brand")
    @Override
    public void read(ByteBuf buf, NetClient client) {
        TridentPlayer player = client.getPlayer();
        String channel = rstr(buf);

        ServerThreadPool.forSpec(PoolSpec.PLUGINS).execute(() -> {
            buf.markReaderIndex();
            byte[] arr = NetData.arr(buf);
            for (SimpleChannelListener listener : TridentPluginChannel.getListeners().values()) {
                listener.messageReceived(channel, player, arr);
            }
            buf.resetReaderIndex();
        });

        if ("MC|Brand".equals(channel)) {
            String brand = rstr(buf);
            System.out.println("User [" + client.getName() + "] is running client [" + brand + "]");
            return;
        }

        if (channel.equals(TridentPluginChannel.REGISTER)) {
            while (buf.isReadable()) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                byte b = buf.readByte();
                while (b != 0x00) {
                    stream.write(b);
                    if (!buf.isReadable()) {
                        break;
                    }

                    b = buf.readByte();
                }

                String name = new String(stream.toByteArray(), NetData.NET_CHARSET);
                PluginChannel c = TridentPluginChannel.getChannel(name, TridentPluginChannel::new);
                c.addRecipient(player);
            }
            return;
        }

        if (channel.equals(TridentPluginChannel.UNREGISTER)) {
            while (buf.isReadable()) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                byte b = buf.readByte();
                while (b != 0x00) {
                    stream.write(b);
                    if (!buf.isReadable()) {
                        break;
                    }

                    b = buf.readByte();
                }

                String name = new String(stream.toByteArray(), NetData.NET_CHARSET);
                PluginChannel c = TridentPluginChannel.get(name);
                if (c != null) {
                    c.closeFor(player.getUuid());
                }
            }
            return;
        }
    }
}
