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
package net.tridentsdk.api.nbt;

import com.google.common.base.Charsets;

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
        TagType initType = TagType.fromId(b);

        //NBT source must start with a compound tag or is invalid
        if (initType != TagType.COMPOUND) {
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
        TagType innerType;

        while ((innerType = TagType.fromId(this.input.readByte())) != TagType.END) {
            compound.addTag(this.resolveTag(innerType, true));
        }

        return compound;
    }

    private ListTag resolveListTag(String name) throws IOException {
        TagType listType = TagType.fromId(this.input.readByte());
        ListTag list = new ListTag(name, listType);
        int length = this.input.readInt();

        for (int i = 0; i < length; i++) {
            list.addTag(this.resolveTag(listType, false));
        }

        return list;
    }

    private NBTTag resolveTag(TagType type, boolean withName) throws IOException {

        //Reads name if required
        String name = null;
        if (withName) {
            name = this.readString();
        }

        NBTTag result;
        switch (type) {
            case BYTE:
                result = new ByteTag(name);
                result.asType(ByteTag.class).setValue(this.input.readByte());
                break;

            case SHORT:
                result = new ShortTag(name);
                result.asType(ShortTag.class).setValue(this.input.readShort());
                break;

            case INT:
                result = new IntTag(name);
                result.asType(IntTag.class).setValue(this.input.readInt());
                break;

            case LONG:
                result = new LongTag(name);
                result.asType(LongTag.class).setValue(this.input.readLong());
                break;

            case FLOAT:
                result = new FloatTag(name);
                result.asType(FloatTag.class).setValue(this.input.readFloat());
                break;

            case DOUBLE:
                result = new DoubleTag(name);
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
                result = new StringTag(name);
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
