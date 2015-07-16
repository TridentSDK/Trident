/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
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

package net.tridentsdk.server.packets.status;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.tridentsdk.DisplayInfo;
import net.tridentsdk.Trident;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

/**
 * Status response to PacketStatusInRequest
 *
 * @author The TridentSDK Team
 * @see PacketStatusInRequest
 */
public class PacketStatusOutResponse extends OutPacket {
    /**
     * The actual response, represented in JSON in the protocol
     */
    DisplayInfo info;

    public PacketStatusOutResponse() {
        this.info = Trident.server().getDisplayInfo();
    }

    @Override
    public int id() {
        return 0x00;
    }

    public DisplayInfo info() {
        return info;
    }

    @Override
    public void encode(ByteBuf buf) {
        // TODO event

        JsonObject object = new JsonObject();
        JsonObject players = new JsonObject();
        JsonObject version = new JsonObject();
        JsonObject motd = new JsonObject();

        players.add("max", new JsonPrimitive(info.getMaxPlayers()));
        players.add("online", new JsonPrimitive(info.getPlayerCount()));

        version.add("name", new JsonPrimitive(info.getVersion()));
        version.add("protocol", new JsonPrimitive(47));

        motd.add("text", new JsonPrimitive(info.getMotd()));

        object.add("players", players);
        object.add("version", version);
        object.add("description", motd);

        Codec.writeString(buf, object.toString());
    }
}
