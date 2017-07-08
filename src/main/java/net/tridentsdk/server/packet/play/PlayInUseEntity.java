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
package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.PacketIn;

import javax.annotation.concurrent.Immutable;

import static net.tridentsdk.server.net.NetData.rvint;

/**
 * Sent by the client to the server whenever a player
 * interacts with an entity.
 */
@Immutable
public class PlayInUseEntity extends PacketIn {
    public PlayInUseEntity() {
        super(PlayInUseEntity.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        int target = rvint(buf); // eid I think
        int type = rvint(buf); // 0=interact 1=attack 2=inat
        float x = Float.NaN;
        float y = Float.NaN;
        float z = Float.NaN;
        int hand = -1;
        if (type == 2) {
            x = buf.readFloat();
            y = buf.readFloat();
            z = buf.readFloat();
        }

        if (type == 0 || type == 2) {
            hand = rvint(buf); // 0=main
        }
    }
}