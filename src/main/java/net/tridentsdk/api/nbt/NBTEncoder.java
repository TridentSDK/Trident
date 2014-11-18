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

import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

/**
 * @author The TridentSDK Team
 */
public class NBTEncoder {
    final DataOutput output;

    public NBTEncoder(DataOutput output) {
        this.output = output;
    }

    public void encode(CompoundTag tag) throws NBTException {
        try {
            this.writeTag(tag);
        } catch (IOException e) {
            throw new NBTException("IO Error encoding the NBT Data", e);
        }
    }

    private void writeCompoundTag(CompoundTag tag) throws IOException {
        for (NBTTag inner : tag.listTags()) {
            this.writeTag(inner);
        }
        //Write Tag_End to signify end of Compound
        this.output.writeByte(TagType.END.getId());
    }

    private void writeListTag(ListTag tag) throws IOException {
        //Write inner tag-type id
        this.output.writeByte(tag.getInnerType().getId());

        List<NBTTag> innerTags = tag.listTags();
        this.output.writeInt(innerTags.size());

        for (NBTTag inner : innerTags) {
            this.writeTag(inner);
        }
    }

    private void writeTag(NBTTag tag) throws IOException {
        this.output.writeByte(tag.getType().getId());

        if (tag.hasName()) {
            this.writeString(tag.getName());
        }

        switch (tag.getType()) {
            case BYTE:
                this.output.writeByte((int) tag.asType(ByteTag.class).getValue());
                break;

            case SHORT:
                this.output.writeShort((int) tag.asType(ShortTag.class).getValue());
                break;

            case INT:
                this.output.writeInt(tag.asType(IntTag.class).getValue());
                break;

            case LONG:
                this.output.writeLong(tag.asType(LongTag.class).getValue());
                break;

            case FLOAT:
                this.output.writeFloat(tag.asType(FloatTag.class).getValue());
                break;

            case DOUBLE:
                this.output.writeDouble(tag.asType(DoubleTag.class).getValue());
                break;

            case BYTE_ARRAY:
                byte[] barray = tag.asType(ByteArrayTag.class).getValue();
                this.output.writeInt(barray.length);
                this.output.write(barray);
                break;

            case STRING:
                this.writeString(tag.asType(StringTag.class).getValue());
                break;

            case LIST:
                this.writeListTag(tag.asType(ListTag.class));
                break;

            case COMPOUND:
                this.writeCompoundTag(tag.asType(CompoundTag.class));
                break;

            case INT_ARRAY:
                int[] iarray = tag.asType(IntArrayTag.class).getValue();
                this.output.writeInt(iarray.length);
                for (int anIarray : iarray) {
                    this.output.writeInt(anIarray);
                }
                break;

            default:
                //Shouldn't/Can't happen
                break;
        }
    }

    private void writeString(String s) throws IOException {
        this.output.writeShort(s.length());
        this.output.write(s.getBytes(Charsets.UTF_8));
    }
}
