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

import net.tridentsdk.api.TridentFactory;
import net.tridentsdk.api.nbt.*;
import net.tridentsdk.api.nbt.ByteArrayTag;
import net.tridentsdk.api.nbt.ByteTag;
import net.tridentsdk.api.nbt.CompoundTag;
import net.tridentsdk.api.nbt.DoubleTag;
import net.tridentsdk.api.nbt.FloatTag;
import net.tridentsdk.api.nbt.IntArrayTag;
import net.tridentsdk.api.nbt.IntTag;
import net.tridentsdk.api.nbt.ListTag;
import net.tridentsdk.api.nbt.LongTag;
import net.tridentsdk.api.nbt.NBTField;
import net.tridentsdk.api.nbt.NullTag;
import net.tridentsdk.api.nbt.ShortTag;
import net.tridentsdk.api.nbt.StringTag;
import net.tridentsdk.api.nbt.TagType;
import net.tridentsdk.api.nbt.builder.CompoundTagBuilder;
import net.tridentsdk.api.nbt.builder.NBTBuilder;
import net.tridentsdk.api.reflect.FastClass;
import net.tridentsdk.api.reflect.FastField;
import net.tridentsdk.api.util.StringUtil;

import java.lang.reflect.Field;

public final class NBTSerializer {

    private NBTSerializer() {
    }

    public static <T> T deserialize(Class<T> clzz, net.tridentsdk.api.nbt.CompoundTag tag) {
        if (!(NBTSerializable.class.isAssignableFrom(clzz))) {
            throw new IllegalArgumentException("Provided object is not serializable!");
        }

        FastClass cls = FastClass.get(clzz);
        T instance = cls.getConstructor().newInstance();

        return deserialize(instance, tag);
    }

    public static <T> T deserialize(T instance, net.tridentsdk.api.nbt.CompoundTag tag) {
        if (!(NBTSerializable.class.isAssignableFrom(instance.getClass()))) {
            throw new IllegalArgumentException("Provided object is not serializable!");
        }

        FastClass cls = FastClass.get(instance.getClass());

        for (FastField field : cls.getFields(instance)) {
            Field f = field.toField();

            if (!f.isAnnotationPresent(net.tridentsdk.api.nbt.NBTField.class)) {
                continue;
            }

            String tagName = f.getAnnotation(net.tridentsdk.api.nbt.NBTField.class).name();
            net.tridentsdk.api.nbt.TagType type = f.getAnnotation(net.tridentsdk.api.nbt.NBTField.class).type();
            net.tridentsdk.api.nbt.NBTTag value;

            if (!tag.containsTag(tagName)) {
                value = new NullTag(tagName);
            } else {
                value = tag.getTag(tagName);
            }

            if (value.getType() != type) {
                new IllegalArgumentException(StringUtil.concat(tagName, "'s tag type ", type,
                        " is not applicable to ", value.getType(), "! Ignoring...")).printStackTrace();
                continue;
            }

            switch (value.getType()) {
                case BYTE:
                    field.set(value.asType(ByteTag.class).getValue());
                    break;

                case BYTE_ARRAY:
                    field.set(value.asType(ByteArrayTag.class).getValue());
                    break;

                case COMPOUND:
                    field.set(value);
                    break;

                case DOUBLE:
                    field.set(value.asType(DoubleTag.class).getValue());
                    break;

                case FLOAT:
                    field.set(value.asType(FloatTag.class).getValue());
                    break;

                case INT:
                    field.set(value.asType(IntTag.class).getValue());
                    break;

                case INT_ARRAY:
                    field.set(value.asType(IntArrayTag.class).getValue());
                    break;

                case LONG:
                    field.set(value.asType(LongTag.class).getValue());
                    break;

                case SHORT:
                    field.set(value.asType(ShortTag.class).getValue());
                    break;

                case LIST:
                    field.set(value.asType(net.tridentsdk.api.nbt.ListTag.class));
                    break;

                case STRING:
                    field.set(value.asType(StringTag.class).getValue());
                    break;

                case NULL:
                    field.set(null);
                    break;

                default:
                    break;
            }
        }

        return instance;
    }

    public static net.tridentsdk.api.nbt.CompoundTag serialize(NBTSerializable serializable, String name) {
        FastClass cls = FastClass.get(serializable.getClass());
        CompoundTagBuilder<NBTBuilder> builder =
                TridentFactory.createNbtBuilder(name);

        for (FastField field : cls.getFields(serializable)) {
            Field f = field.toField();

            if (!f.isAnnotationPresent(net.tridentsdk.api.nbt.NBTField.class)) {
                continue;
            }

            String tagName = f.getAnnotation(net.tridentsdk.api.nbt.NBTField.class).name();
            TagType tagType = f.getAnnotation(NBTField.class).type();
            Object value = field.get();

            switch (tagType) {
                case BYTE:
                    builder.byteTag(tagName, (byte) value);
                    break;

                case BYTE_ARRAY:
                    builder.byteArrayTag(tagName, (byte[]) value);
                    break;

                case COMPOUND:
                    builder.compoundTag((net.tridentsdk.api.nbt.CompoundTag) value);
                    break;

                case DOUBLE:
                    builder.doubleTag(tagName, (double) value);
                    break;

                case FLOAT:
                    builder.floatTag(tagName, (float) value);
                    break;

                case INT:
                    builder.intTag(tagName, (int) value);
                    break;

                case INT_ARRAY:
                    builder.intArrayTag(tagName, (int[]) value);
                    break;

                case LONG:
                    builder.longTag(tagName, (long) value);
                    break;

                case SHORT:
                    builder.shortTag(tagName, (short) value);
                    break;

                case LIST:
                    builder.listTag((ListTag) value);
                    break;

                case STRING:
                    builder.stringTag(tagName, (String) value);
                    break;

                case NULL:
                    builder.nullTag(tagName);
                    break;

                default:
                    break;
            }
        }

        return builder.endCompoundTag().build();
    }

    public static CompoundTag serialize(NBTSerializable serializable) {
        return serialize(serializable, serializable.getClass().getSimpleName());
    }
}
