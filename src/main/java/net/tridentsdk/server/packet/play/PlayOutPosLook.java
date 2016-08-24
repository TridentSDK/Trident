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
import net.tridentsdk.server.packet.PacketOut;
import net.tridentsdk.server.player.TridentPlayer;

import javax.annotation.concurrent.Immutable;

import static net.tridentsdk.server.net.NetData.wvint;

/**
 * Sent after receiving {@link PlayInSettings} from the
 * client, at which point this packet will be sent with the
 * player's position and the client will leave the login
 * screen.
 */
@Immutable
public final class PlayOutPosLook extends PacketOut {
    /**
     * The player to move
     */
    private final TridentPlayer player;
    /**
     * The position which to move the player
     */
    private final Position pos;

    public PlayOutPosLook(TridentPlayer player) {
        super(PlayOutPosLook.class);
        this.player = player;
        this.pos = player.position();
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeDouble(this.pos.x());
        buf.writeDouble(this.pos.y());
        buf.writeDouble(this.pos.z());
        buf.writeFloat(this.pos.yaw());
        buf.writeFloat(this.pos.pitch());
        buf.writeByte(0);
        wvint(buf, PlayInTeleportConfirm.query(this.player.net()));
    }
}