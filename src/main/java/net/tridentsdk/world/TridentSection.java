package net.tridentsdk.world;

import net.tridentsdk.api.nbt.CompoundTag;
import net.tridentsdk.api.nbt.NBTField;
import net.tridentsdk.api.nbt.NBTSerializable;
import net.tridentsdk.api.nbt.TagType;

public class TridentSection implements NBTSerializable{
    private CompoundTag section;

    @NBTField(name = "Y", type = TagType.BYTE)
    protected byte y;

    @NBTField(name = "Blocks", type = TagType.BYTE_ARRAY)
    protected byte[] blocks;

    @NBTField(name = "Add", type = TagType.BYTE_ARRAY)
    protected byte[] additionalData;

    @NBTField(name = "Data", type = TagType.BYTE_ARRAY)
    protected byte[] blockData;

    @NBTField(name = "BlockLight", type = TagType.BYTE_ARRAY)
    protected byte[] blockLight;

    @NBTField(name = "SkyLight", type = TagType.BYTE_ARRAY)
    protected byte[] skyLight;
}
