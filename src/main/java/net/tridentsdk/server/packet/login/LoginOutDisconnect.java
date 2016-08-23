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
package net.tridentsdk.server.packet.login;

import com.google.gson.JsonObject;
import net.tridentsdk.server.config.ConfigIo;
import net.tridentsdk.server.net.NetPayload;
import net.tridentsdk.server.packet.PacketOut;

/**
 * This packet is sent to the client in states LOGIN, PLAY
 * to indicate that the player will be disconnected from
 * the server.
 */
public class LoginOutDisconnect extends PacketOut {
    /**
     * The message displayed on the screen once the player
     * is disconnected
     * TODO chat
     */
    private final String reason;

    public LoginOutDisconnect(String reason) {
        super(LoginOutDisconnect.class);
        this.reason = reason;
    }

    @Override
    public void write(NetPayload payload) {
        JsonObject object = new JsonObject();
        object.addProperty("text", this.reason);
        payload.writeString(ConfigIo.GSON.toJson(object));
        System.out.println("DISCONNECT: " + this.reason);
    }
}