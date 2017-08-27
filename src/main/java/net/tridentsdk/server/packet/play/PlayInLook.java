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

import javax.annotation.concurrent.Immutable;

/**
 * Sent by the player whenever the player turns or looks
 * up and down without moving.
 */
@Immutable
public final class PlayInLook extends PacketIn {
    public PlayInLook() {
        super(PlayInLook.class);
    }

    @Override
    public void read(ByteBuf buf, NetClient client) {
        float yaw = buf.readFloat();
        float pitch = buf.readFloat();
        boolean onGround = buf.readBoolean();

        TridentPlayer player = client.getPlayer();
        if (player == null) return;
        player.setPosition(player.getPosition().setYaw(yaw).setPitch(pitch), false);
        player.setOnGround(onGround);
    }
}
