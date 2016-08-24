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

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;
import java.util.UUID;

import static net.tridentsdk.server.net.NetData.wstr;

/**
 * This packet is sent by the server to indicate to the
 * Minecraft client that login has sucessfully completed.
 */
@Immutable
public final class LoginOutSuccess extends PacketOut {
    /**
     * The client that is successfully logging in
     */
    private final NetClient client;
    /**
     * The UUID of the player to be joined
     */
    private final UUID uuid;
    /**
     * The name of the player
     */
    private final String name;

    public LoginOutSuccess(NetClient client) {
        super(LoginOutSuccess.class);
        this.client = client;
        this.name = client.name();
        UUID temp = Login.uuidFor(this.name);
        if (temp == null) {
            temp = UUID.randomUUID();
        }

        this.uuid = temp;
        client.enableCompression();
    }

    public LoginOutSuccess(NetClient client, UUID uuid, String name) {
        super(LoginOutSuccess.class);
        this.client = client;
        this.uuid = uuid;
        this.name = name;
        client.enableCompression();
    }

    /**
     * Obtains the UUID that will be used for login.
     */
    public UUID uuid() {
        return this.uuid;
    }

    @Override
    public void write(ByteBuf buf) {
        wstr(buf, this.uuid.toString());
        wstr(buf, this.name);
        this.client.setState(NetClient.NetState.PLAY);
    }
}