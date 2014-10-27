package net.tridentsdk.player;

import net.tridentsdk.api.nbt.NBTField;
import net.tridentsdk.api.nbt.NBTSerializable;
import net.tridentsdk.api.nbt.TagType;
import net.tridentsdk.packets.play.out.PacketPlayOutPlayerAbilities;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PlayerAbilities implements NBTSerializable {

    @NBTField(name = "walkSpeed", type = TagType.FLOAT)
    protected float walkingSpeed;
    @NBTField(name = "flySpeed", type = TagType.FLOAT)
    protected float flySpeed;
    @NBTField(name = "canFly", type = TagType.BYTE)
    protected byte canFly;
    @NBTField(name = "flying", type = TagType.BYTE)
    protected byte flying;
    @NBTField(name = "invunerable", type = TagType.BYTE)
    protected byte invulnerable;
    @NBTField(name = "canBuild", type = TagType.BYTE)
    protected byte canBuild;
    @NBTField(name = "instantBuild", type = TagType.BYTE)
    protected byte instantBreak;

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
