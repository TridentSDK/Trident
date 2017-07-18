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
import net.tridentsdk.base.Position;
import net.tridentsdk.server.entity.TridentEntity;
import net.tridentsdk.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;

import static net.tridentsdk.server.net.NetData.convertAngle;
import static net.tridentsdk.server.net.NetData.wvint;

/**
 * Sent by the server whenever a player needs to be moved
 * more than 4 blocks.
 */
@Immutable
public class PlayOutTeleport extends PacketOut {
    private final int eid;
    private final Position position;
    private final boolean onGround;

    public PlayOutTeleport(TridentEntity entity, Position teleport) {
        super(PlayOutTeleport.class);
        this.eid = entity.getId();
        this.position = teleport;
        this.onGround = entity.isOnGround();
    }

    @Override
    public void write(ByteBuf buf) {
        wvint(buf, this.eid);
        buf.writeDouble(this.position.getX());
        buf.writeDouble(this.position.getY());
        buf.writeDouble(this.position.getZ());
        buf.writeByte(convertAngle(this.position.getYaw()));
        buf.writeByte(convertAngle(this.position.getPitch()));
        buf.writeBoolean(this.onGround);
    }
}