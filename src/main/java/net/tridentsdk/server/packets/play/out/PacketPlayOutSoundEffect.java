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
import net.tridentsdk.Location;
import net.tridentsdk.Sound;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PacketPlayOutSoundEffect extends OutPacket {

    protected Sound soundName;
    protected Location loc;
    protected float volume; // f * 100
    protected int pitch; // 63 = 100%

    @Override
    public int getId() {
        return 0x29;
    }

    /**
     * @return Darude - Sandstorm
     */
    public Sound getSoundName() {
        return this.soundName;
    }

    public Location getLoc() {
        return this.loc;
    }

    public float getVolume() {
        return this.volume;
    }

    public int getPitch() {
        return this.pitch;
    }

    @Override
    public void encode(ByteBuf buf) {
        Codec.writeString(buf, this.soundName.toString());

        buf.writeInt((int) this.loc.getX());
        buf.writeInt((int) this.loc.getY());
        buf.writeInt((int) this.loc.getZ());

        buf.writeFloat(this.volume);
        buf.writeByte(this.pitch);
    }
}
