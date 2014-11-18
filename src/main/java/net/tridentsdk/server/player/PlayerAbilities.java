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
package net.tridentsdk.server.player;

import net.tridentsdk.api.nbt.NBTField;
import net.tridentsdk.api.nbt.NBTSerializable;
import net.tridentsdk.api.nbt.TagType;
import net.tridentsdk.server.packets.play.out.PacketPlayOutPlayerAbilities;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PlayerAbilities implements NBTSerializable {

    @NBTField(name = "walkSpeed", type = TagType.FLOAT)
    protected float walkingSpeed = 0F;
    @NBTField(name = "flySpeed", type = TagType.FLOAT)
    protected float flySpeed = 0F;
    @NBTField(name = "canFly", type = TagType.BYTE)
    protected byte canFly = 0;
    @NBTField(name = "flying", type = TagType.BYTE)
    protected byte flying = 0;
    @NBTField(name = "invunerable", type = TagType.BYTE)
    protected byte invulnerable = 0;
    @NBTField(name = "canBuild", type = TagType.BYTE)
    protected byte canBuild = 1;
    @NBTField(name = "instantBuild", type = TagType.BYTE)
    protected byte instantBreak = 0;

    protected PlayerAbilities() {
    }

    public float getWalkingSpeed() {
        return walkingSpeed;
    }

    public float getFlySpeed() {
        return flySpeed;
    }

    public boolean canFly() {
        return canFly == 1;
    }

    public boolean isFlying() {
        return flying == 1;
    }

    public boolean isInvulnerable() {
        return flying == 1;
    }

    public boolean canBuild() {
        return canBuild == 1;
    }

    public boolean canInstantBreak() {
        return instantBreak == 1;
    }

    public OutPacket toPacket() {
        OutPacket packet = new PacketPlayOutPlayerAbilities();
        byte flags = (byte) ((isInvulnerable() ? 8 : 0) | (canFly() ? 4 : 0) | (canInstantBreak() ? 2 : 0));

        packet.set("flags", flags);
        packet.set("flyingSpeed", flySpeed / 2);
        packet.set("walkingSpeed", walkingSpeed / 2);

        return packet;
    }
}
