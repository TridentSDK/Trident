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
import net.tridentsdk.server.player.TridentPlayer;

/**
 * @author Nick Robson
 */
public class PlayInPlayerAbilities extends PacketIn {

    public PlayInPlayerAbilities() {
        super(PlayInPlayerAbilities.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        byte flags = buf.readByte();

        boolean isGod = (flags & 0x08) != 0;
        boolean canFly = (flags & 0x04) != 0;
        boolean isFlying = (flags & 0x02) != 0;
        boolean isCreative = (flags & 0x01) != 0;

        float flyingSpeed = buf.readFloat();
        float walkingSpeed = buf.readFloat();

        // NOTE: We have to be very careful here, since a hacked client can easily send these things.

        TridentPlayer player = client.getPlayer();

        if (player.canFly()) {
            player.setFlying(isFlying, false);
        } else {
            player.setFlying(false, false);
        }

        client.sendPacket(new PlayOutPlayerAbilities(player));
    }
}
