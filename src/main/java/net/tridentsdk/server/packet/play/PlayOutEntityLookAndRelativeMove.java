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
import net.tridentsdk.entity.Entity;
import net.tridentsdk.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;

import static net.tridentsdk.server.net.NetData.wvint;

@Immutable
public final class PlayOutEntityLookAndRelativeMove extends PacketOut {

    private final int id;
    private final double yaw;
    private final double pitch;
    private final boolean onGround;
    private final Position delta;

    public PlayOutEntityLookAndRelativeMove(Entity entity, Position delta) {
        super(PlayOutEntityLookAndRelativeMove.class);
        this.id = entity.getId();
        this.yaw = entity.getPosition().getYaw();
        this.pitch = entity.getPosition().getPitch();
        this.onGround = entity.isOnGround();
        this.delta = delta.clone().multiply(32, 32, 32).multiply(128, 128, 128);
    }

    @Override
    public void write(ByteBuf buf) {
        wvint(buf, id);

        buf.writeShort((int) delta.getX());
        buf.writeShort((int) delta.getY());
        buf.writeShort((int) delta.getZ());

        buf.writeByte((int) (yaw * 256 / 360));
        buf.writeByte((int) (pitch / 1.4));

        buf.writeBoolean(onGround);
    }

}
