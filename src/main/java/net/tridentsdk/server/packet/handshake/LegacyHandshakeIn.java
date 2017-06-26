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
package net.tridentsdk.server.packet.handshake;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.PacketIn;

import javax.annotation.concurrent.Immutable;

/**
 * Legacy handshake packet that is usually sent when the
 * client's regular ping fails.
 */
@Immutable
public class LegacyHandshakeIn extends PacketIn {
    public LegacyHandshakeIn() {
        super(LegacyHandshakeIn.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        if (buf.readUnsignedByte() != 1) {
            throw new RuntimeException("Legacy handshake has the wrong schema");
        }
    }
}
