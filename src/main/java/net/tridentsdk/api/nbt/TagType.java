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
package net.tridentsdk.api.nbt;

/**
 * @author The TridentSDK Team
 */
public enum TagType {
    NULL(-1, NullTag.class),

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
    final Class<? extends NBTTag> implClass;

    TagType(int id, Class<? extends NBTTag> implClass) {
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
