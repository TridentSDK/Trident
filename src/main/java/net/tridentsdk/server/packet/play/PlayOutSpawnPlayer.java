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
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;

import static net.tridentsdk.server.net.NetData.wvint;

@Immutable
public final class PlayOutSpawnPlayer extends PacketOut {

    private final Player player;

    public PlayOutSpawnPlayer(Player player) {
        super(PlayOutSpawnPlayer.class);
        this.player = player;
    }

    @Override
    public void write(ByteBuf buf) {
        wvint(buf, player.getId());

        buf.writeLong(player.uuid().getMostSignificantBits());
        buf.writeLong(player.uuid().getLeastSignificantBits());

        buf.writeDouble(player.getPosition().x());
        buf.writeDouble(player.getPosition().y());
        buf.writeDouble(player.getPosition().z());

        buf.writeByte((byte) ((player.getPosition().yaw()) % 360 * (256d / 360d)));
        buf.writeByte((int) (byte) player.getPosition().pitch());

        buf.writeByte(7);
        buf.writeByte(2);
        buf.writeFloat(20f);
        buf.writeByte(0xFF);
    }

}