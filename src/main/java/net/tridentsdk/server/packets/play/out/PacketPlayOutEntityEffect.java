/*
 *     Trident - A Multithreaded Server Alternative
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.server.packets.play.out;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PacketPlayOutEntityEffect extends OutPacket {

    protected int entityId;
    protected short effectId;
    protected short amplifier;

    protected long duration;
    protected boolean hideParticles;

    @Override
    public int getId() {
        return 0x0D;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public short getEffectId() {
        return this.effectId;
    }

    public short getAmplifier() {
        return this.amplifier;
    }

    public long getDuration() {
        return this.duration;
    }

    public boolean isHideParticles() {
        return this.hideParticles;
    }

    @Override
    public void encode(ByteBuf buf) {
        Codec.writeVarInt32(buf, this.entityId);

        buf.writeByte((int) this.effectId);
        buf.writeByte((int) this.amplifier);
        Codec.writeVarInt64(buf, this.duration);

        buf.writeBoolean(this.hideParticles);
    }
}
