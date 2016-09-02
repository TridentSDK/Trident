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
import net.tridentsdk.entity.Entity;
import net.tridentsdk.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;

import static net.tridentsdk.server.net.NetData.wvint;

@Immutable
public final class PlayOutEntityLook extends PacketOut {

    private final Entity entity;

    public PlayOutEntityLook(Entity entity) {
        super(PlayOutEntityLook.class);
        this.entity = entity;
    }

    @Override
    public void write(ByteBuf buf) {
        wvint(buf, entity.getId());

        buf.writeByte((int) (byte) entity.getPosition().yaw());
        buf.writeByte((int) (((int) (byte) entity.getPosition().pitch()) / 1.4));

        buf.writeBoolean(true);
    }

}