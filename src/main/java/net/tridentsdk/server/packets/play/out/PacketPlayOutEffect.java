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
import net.tridentsdk.api.Location;
import net.tridentsdk.server.data.Position;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PacketPlayOutEffect extends OutPacket {

    protected int effectId;
    protected Location loc;
    protected int data;
    protected boolean playSound;

    @Override
    public int getId() {
        return 0x28;
    }

    public int getEffectId() {
        return this.effectId;
    }

    public Location getLoc() {
        return this.loc;
    }

    public int getData() {
        return this.data;
    }

    public boolean isPlaySound() {
        return this.playSound;
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeInt(this.effectId);

        new Position(this.loc).write(buf);

        buf.writeInt(this.data);
        buf.writeBoolean(this.playSound);
    }
}
