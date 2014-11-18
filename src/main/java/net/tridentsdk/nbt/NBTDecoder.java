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

import com.google.common.base.Charsets;
import net.tridentsdk.api.nbt.*;
import net.tridentsdk.api.nbt.ByteTag;
import net.tridentsdk.api.nbt.DoubleTag;
import net.tridentsdk.api.nbt.FloatTag;
import net.tridentsdk.api.nbt.IntTag;
import net.tridentsdk.api.nbt.LongTag;
import net.tridentsdk.api.nbt.NBTTag;
import net.tridentsdk.api.nbt.NullTag;
import net.tridentsdk.api.nbt.ShortTag;
import net.tridentsdk.api.nbt.StringTag;
import net.tridentsdk.api.nbt.TagType;

import java.io.DataInput;
import java.io.IOException;

/**
 * @author The TridentSDK Team
 */
public class NBTDecoder {
    final DataInput input;

    public NBTDecoder(DataInput input) {
        this.input = input;
    }

    public CompoundTag decode() throws NBTException {
        try {
            return this.decode(this.input.readByte());
        } catch (IOException e) {
            throw new NBTException("IO Error decoding the NBT Data", e);
        }
    }

    public CompoundTag decode(byte b) throws NBTException {
        net.tridentsdk.api.nbt.TagType initType = net.tridentsdk.api.nbt.TagType.fromId(b);

        //NBT source must start with a compound tag or is invalid
        if (initType != net.tridentsdk.api.nbt.TagType.COMPOUND) {
            throw new NBTException("NBT Data must start with a Compound Tag.");
        }

        //Create the resulting CompoundTag to return
        //Uses recursion to recursively walk through the tag tree
        try {
            return this.resolveCompoundTag(this.readString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new NBTException("IO Error decoding the NBT Data", e);
        }
    }

    private CompoundTag resolveCompoundTag(String name) throws IOException {
        CompoundTag compound = new CompoundTag(name);
        net.tridentsdk.api.nbt.TagType innerType;

        while ((innerType = net.tridentsdk.api.nbt.TagType.fromId(this.input.readByte())) != net.tridentsdk.api.nbt.TagType.END) {
            compound.addTag(this.resolveTag(innerType, true));
        }

        return compound;
    }

    private net.tridentsdk.api.nbt.ListTag resolveListTag(String name) throws IOException {
        net.tridentsdk.api.nbt.TagType listType = net.tridentsdk.api.nbt.TagType.fromId(this.input.readByte());
        net.tridentsdk.api.nbt.ListTag list = new net.tridentsdk.api.nbt.ListTag(name, listType);
        int length = this.input.readInt();

        for (int i = 0; i < length; i++) {
            list.addTag(this.resolveTag(listType, false));
        }

        return list;
    }

    private net.tridentsdk.api.nbt.NBTTag resolveTag(TagType type, boolean withName) throws IOException {

        //Reads name if required
        String name = null;
        if (withName) {
            name = this.readString();
        }

        NBTTag result;
        switch (type) {
            case BYTE:
                result = new net.tridentsdk.api.nbt.ByteTag(name);
                result.asType(ByteTag.class).setValue(this.input.readByte());
                break;

            case SHORT:
                result = new net.tridentsdk.api.nbt.ShortTag(name);
                result.asType(ShortTag.class).setValue(this.input.readShort());
                break;

            case INT:
                result = new net.tridentsdk.api.nbt.IntTag(name);
                result.asType(IntTag.class).setValue(this.input.readInt());
                break;

            case LONG:
                result = new net.tridentsdk.api.nbt.LongTag(name);
                result.asType(LongTag.class).setValue(this.input.readLong());
                break;

            case FLOAT:
                result = new net.tridentsdk.api.nbt.FloatTag(name);
                result.asType(FloatTag.class).setValue(this.input.readFloat());
                break;

            case DOUBLE:
                result = new net.tridentsdk.api.nbt.DoubleTag(name);
                result.asType(DoubleTag.class).setValue(this.input.readDouble());
                break;

            case BYTE_ARRAY:
                result = new ByteArrayTag(name);
                int balength = this.input.readInt();
                byte[] babytes = new byte[balength];

                this.input.readFully(babytes);
                result.asType(ByteArrayTag.class).setValue(babytes);

                break;

            case STRING:
                result = new net.tridentsdk.api.nbt.StringTag(name);
                result.asType(StringTag.class).setValue(this.readString());

                break;

            case LIST:
                result = this.resolveListTag(name);
                break;

            case COMPOUND:
                result = this.resolveCompoundTag(name);
                break;

            case INT_ARRAY:
                result = new IntArrayTag(name);
                int ialength = this.input.readInt();
                int[] array = new int[ialength];

                for (int i = 0; i < array.length; i++) {
                    array[i] = this.input.readInt();
                }

                result.asType(IntArrayTag.class).setValue(array);
                break;

            default:
                result = new NullTag(name);
                break;
        }

        return result;
    }

    private String readString() throws IOException {
        short length = this.input.readShort();
        byte[] bytes = new byte[(int) length];

        this.input.readFully(bytes);

        return new String(bytes, Charsets.UTF_8);
    }
}