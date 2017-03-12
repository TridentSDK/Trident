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
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.PacketIn;

import javax.annotation.concurrent.Immutable;

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
        client.sendPacket(new StatusOutResponse());
    }
}