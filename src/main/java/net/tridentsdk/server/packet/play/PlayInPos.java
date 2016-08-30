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
package net.tridentsdk.server.packet.play;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.base.Position;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.net.NetClient;
import net.tridentsdk.server.packet.PacketIn;
import net.tridentsdk.server.player.TridentPlayer;

import javax.annotation.concurrent.Immutable;

/**
 * Packet received by the server upon the client requesting
 * the player entity to move.
 */
@Immutable
public final class PlayInPos extends PacketIn {
    public PlayInPos() {
        super(PlayInPos.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        double x = buf.readDouble();
        double feetY = buf.readDouble();
        double z = buf.readDouble();
        boolean onGround = buf.readBoolean();

        TridentPlayer player = client.player();
        Position position = player.position();
        Position oldPos = position.clone();

        position.setX(x);
        position.setY(feetY);
        position.setZ(z);
        player.setOnGround(onGround);

        Position delta = position.clone().subtract(oldPos);

        if(delta.x() != 0 || delta.y() != 0 || delta.z() != 0) {
            PlayOutEntityRelativeMove packet = new PlayOutEntityRelativeMove(player, delta);
            TridentServer.instance().players().stream().filter(p -> !p.equals(player)).forEach(p -> ((TridentPlayer) p).net().sendPacket(packet));
        }
    }
}