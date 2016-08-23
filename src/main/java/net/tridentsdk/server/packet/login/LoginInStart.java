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

import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.net.NetCrypto;
import net.tridentsdk.server.net.NetPayload;
import net.tridentsdk.server.packet.PacketIn;

/**
 * Login start request made after status switch to 2 due to
 * handshake request.
 */
public class LoginInStart extends PacketIn {
    public LoginInStart() {
        super(LoginInStart.class);
    }

    @Override
    public void read(NetPayload payload, NetClient sender) {
        String name = payload.readString();
        sender.setName(name);
        // TODO check join parameters
        // TODO player join event

        if (TridentServer.cfg().doAuth()) {
            NetCrypto crypto = sender.initCrypto();
            sender.sendPacket(crypto.reqCrypto());
        } else {
            sender.sendPacket(new LoginOutSuccess(sender));
        }
    }
}