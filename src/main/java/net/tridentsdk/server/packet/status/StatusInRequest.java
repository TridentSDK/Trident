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
package net.tridentsdk.server.packet.status;

import io.netty.buffer.ByteBuf;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import net.tridentsdk.event.server.ServerPingEvent;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.concurrent.PoolSpec;
import net.tridentsdk.server.concurrent.ServerThreadPool;
import net.tridentsdk.server.config.ServerConfig;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.PacketIn;

import javax.annotation.concurrent.Immutable;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.ui.chat.ChatComponent;

import static net.tridentsdk.server.packet.status.StatusOutResponse.MC_VERSION;
import static net.tridentsdk.server.packet.status.StatusOutResponse.PROTOCOL_VERSION;

/**
 * This packet represents a status request that is sent
 * when
 * the server is pinged and after the server receives a
 * handshake packet with a next status set to 1.
 */
@Immutable
public final class StatusInRequest extends PacketIn {
    public StatusInRequest() {
        super(StatusInRequest.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        ServerConfig cfg = TridentServer.cfg();

        Collection<TridentPlayer> players = TridentPlayer.getPlayers().values();
        int onlinePlayers = players.size();
        ServerPingEvent.ServerPingResponseSample[] sample = new ServerPingEvent.ServerPingResponseSample[onlinePlayers];
        int i = 0;
        for (TridentPlayer player : players) {
            sample[i++] = new ServerPingEvent.ServerPingResponseSample(player.getName(), player.getUuid());
        }

        ServerPingEvent.ServerPingResponse response = new ServerPingEvent.ServerPingResponse(
                new ServerPingEvent.ServerPingResponseVersion(MC_VERSION, PROTOCOL_VERSION),
                new ServerPingEvent.ServerPingResponsePlayers(onlinePlayers, cfg.maxPlayers(), sample),
                ChatComponent.text(cfg.motd()),
                StatusOutResponse.b64icon.get()
        );
        InetSocketAddress pinger = (InetSocketAddress) client.getChannel().remoteAddress();
        ServerPingEvent event = new ServerPingEvent(pinger, response);
        ServerThreadPool.forSpec(PoolSpec.PLUGINS)
                .submit(() -> TridentServer.getInstance().getEventController()
                        .dispatch(event, e -> client.sendPacket(new StatusOutResponse(e)))
        );
    }
}
