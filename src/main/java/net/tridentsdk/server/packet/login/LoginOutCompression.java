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
import net.tridentsdk.server.net.NetPayload;
import net.tridentsdk.server.packet.PacketOut;

/**
 * Indicates to the client that the server requests to
 * compress packets.
 */
public class LoginOutCompression extends PacketOut {
    public LoginOutCompression() {
        super(LoginOutCompression.class);
    }

    @Override
    public void write(NetPayload payload) {
        payload.writeVInt(TridentServer.cfg().compressionThresh());
    }
}
