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
package net.tridentsdk.world;

import net.tridentsdk.api.nbt.CompoundTag;
import net.tridentsdk.api.nbt.NBTField;
import net.tridentsdk.api.nbt.NBTSerializable;
import net.tridentsdk.api.nbt.TagType;

public class TridentSection implements NBTSerializable {
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
