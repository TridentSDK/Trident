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
package net.tridentsdk.server.packet.status;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import net.tridentsdk.chat.ChatComponent;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.config.ServerConfig;
import net.tridentsdk.server.packet.PacketOut;
import net.tridentsdk.server.player.TridentPlayer;

import javax.annotation.concurrent.Immutable;

import static net.tridentsdk.server.net.NetData.wstr;

/**
 * The response to a client ping request.
 */
@Immutable
public final class StatusOutResponse extends PacketOut {
    /**
     * The current Minecraft version implemented by the
     * server
     */
    public static final String MC_VERSION = "1.11";
    /**
     * The protocol version associated with the Minecraft
     * version
     */
    private static final int PROTOCOL_VERSION = 315;

    public StatusOutResponse() {
        super(StatusOutResponse.class);
    }

    @Override
    public void write(ByteBuf buf) {
        ServerConfig cfg = TridentServer.cfg();
        JsonObject resp = new JsonObject();

        JsonObject version = new JsonObject();
        version.addProperty("name", MC_VERSION);
        version.addProperty("protocol", PROTOCOL_VERSION);
        resp.add("version", version);

        JsonObject players = new JsonObject();
        players.addProperty("max", cfg.maxPlayers());
        players.addProperty("online", TridentPlayer.PLAYERS.size());
        JsonArray sample = new JsonArray();
        TridentPlayer.PLAYERS.forEach((u, p) -> {
            JsonObject o = new JsonObject();
            o.addProperty("name", p.name());
            o.addProperty("id", p.uuid().toString());
            sample.add(o);
        });
        players.add("sample", sample);
        resp.add("players", players);

        resp.add("description", ChatComponent.text(cfg.motd()).asJson());

        // resp.addProperty("favicon", "data:image/png;base64,<data>");
        //String toString = ConfigIo.GSON.toJson(resp);
        wstr(buf, resp.toString());
    }
}
