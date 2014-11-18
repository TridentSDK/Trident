/*
 *     TridentSDK - A Minecraft Server API
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
package net.tridentsdk.nbt;

import net.tridentsdk.api.nbt.*;
import net.tridentsdk.api.nbt.ByteTag;
import net.tridentsdk.api.nbt.IntTag;
import net.tridentsdk.api.nbt.ListTag;
import net.tridentsdk.api.nbt.LongTag;
import net.tridentsdk.api.nbt.NBTTag;

/**
 * @author The TridentSDK Team
 */
public enum TagType {
    NULL(-1, net.tridentsdk.api.nbt.NullTag.class),

    END(0, null),

    BYTE(1, ByteTag.class),

    SHORT(2, ShortTag.class),

    INT(3, IntTag.class),

    LONG(4, LongTag.class),

    FLOAT(5, FloatTag.class),

    DOUBLE(6, DoubleTag.class),

    BYTE_ARRAY(7, ByteArrayTag.class),

    STRING(8, StringTag.class),

    LIST(9, ListTag.class),

    COMPOUND(10, CompoundTag.class),

    INT_ARRAY(11, IntArrayTag.class);

    final int id;
    final Class<? extends net.tridentsdk.api.nbt.NBTTag> implClass;

    TagType(int id, Class<? extends net.tridentsdk.api.nbt.NBTTag> implClass) {
        this.id = id;
        this.implClass = implClass;
    }

    public static TagType fromId(byte fromId) {
        for (TagType type : TagType.values()) {
            if (type.id == fromId) {
                return type;
            }
        }
        return NULL;
    }

    public Class<? extends NBTTag> getImplementation() {
        return this.implClass;
    }

    public int getId() {
        return this.id;
    }
}
