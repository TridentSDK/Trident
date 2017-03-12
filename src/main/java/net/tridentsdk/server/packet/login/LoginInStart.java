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
package net.tridentsdk.server.packet.login;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.net.NetCrypto;
import net.tridentsdk.server.packet.PacketIn;
import net.tridentsdk.server.player.TridentPlayer;

import javax.annotation.concurrent.Immutable;

import static net.tridentsdk.server.net.NetData.rstr;

/**
 * Login start request made after status switch to 2 due to
 * handshake request.
 */
@Immutable
public final class LoginInStart extends PacketIn {
    public LoginInStart() {
        super(LoginInStart.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        if (!Login.canLogin(client)) {
            client.disconnect("Server is full");
            return;
        }

        String name = rstr(buf);
        client.setName(name);
        // TODO player join event

        if (TridentServer.cfg().doAuth()) {
            NetCrypto crypto = client.initCrypto();
            client.sendPacket(crypto.reqCrypto());
        } else {
            LoginOutSuccess packet = new LoginOutSuccess(client);
            client.sendPacket(packet).addListener(
                    future -> {
                        TridentPlayer.spawn(client, name, packet.getUuid(), packet.getTextures());
                        Login.finish();
                    });
        }
    }
}