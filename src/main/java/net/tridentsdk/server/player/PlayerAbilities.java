/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
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

package net.tridentsdk.server.player;

import net.tridentsdk.meta.nbt.NBTField;
import net.tridentsdk.meta.nbt.NBTSerializable;
import net.tridentsdk.meta.nbt.TagType;
import net.tridentsdk.server.netty.packet.OutPacket;
import net.tridentsdk.server.packets.play.out.PacketPlayOutPlayerAbilities;

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
    protected byte creative = 0;

    protected PlayerAbilities() {
    }

    public float walkingSpeed() {
        return walkingSpeed;
    }

    public float flyingSpeed() {
        return flySpeed;
    }

    public boolean canFly() {
        return canFly == 1;
    }

    public boolean isFlying() {
        return flying == 1;
    }

    public boolean isInvulnerable() {
        return invulnerable == 1;
    }

    public boolean canBuild() {
        return canBuild == 1;
    }

    public boolean isCreative() {
        return creative == 1;
    }

    public OutPacket asPacket() {
        OutPacket packet = new PacketPlayOutPlayerAbilities();
        byte flags = (byte) ((isInvulnerable() ? 8 : 0) | (canFly() ? 4 : 0) | (isFlying() ? 2 : 0) |
                (isCreative() ? 1 : 0));

        packet.set("flags", flags);
        packet.set("flyingSpeed", flySpeed / 2);
        packet.set("walkingSpeed", walkingSpeed / 2);

        return packet;
    }
}
