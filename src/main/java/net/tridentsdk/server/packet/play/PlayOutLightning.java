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
import net.tridentsdk.base.AbstractVector;
import net.tridentsdk.server.entity.TridentEntity;
import net.tridentsdk.server.net.NetData;
import net.tridentsdk.server.packet.PacketOut;

import javax.annotation.concurrent.Immutable;

/**
 * Sent by the server to spawn a lightning packet for a
 * particular player.
 */
@Immutable
public final class PlayOutLightning extends PacketOut {
    private final double x;
    private final double y;
    private final double z;

    public PlayOutLightning(AbstractVector<?> vector) {
        super(PlayOutLightning.class);
        this.x = vector.getX();
        this.y = vector.getY();
        this.z = vector.getZ();
    }

    @Override
    public void write(ByteBuf buf) {
        NetData.wvint(buf, TridentEntity.EID_COUNTER.incrementAndGet());
        buf.writeByte(1); // apparently the only useful value
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
    }
}