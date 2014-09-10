/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *     3. Neither the name of TridentSDK nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.data;

import java.util.List;

import net.tridentsdk.api.nbt.ByteArrayTag;
import net.tridentsdk.api.nbt.ByteTag;
import net.tridentsdk.api.nbt.CompoundTag;
import net.tridentsdk.api.nbt.DoubleTag;
import net.tridentsdk.api.nbt.FloatTag;
import net.tridentsdk.api.nbt.IntArrayTag;
import net.tridentsdk.api.nbt.IntTag;
import net.tridentsdk.api.nbt.ListTag;
import net.tridentsdk.api.nbt.LongTag;
import net.tridentsdk.api.nbt.NBTTag;
import net.tridentsdk.api.nbt.ShortTag;
import net.tridentsdk.api.nbt.StringTag;
import net.tridentsdk.api.nbt.TagType;
import net.tridentsdk.server.netty.Codec;
import io.netty.buffer.ByteBuf;

/**
 * @author The TridentSDK Team
 */
public class NBTEncoder {
    final ByteBuf input;
    
    public NBTEncoder(ByteBuf input) {
        this.input = input;
    }
    
    public void encode(CompoundTag tag) {
        writeTag(tag);
    }
    
    private void writeCompoundTag(CompoundTag tag) {
        for (NBTTag inner : tag.listTags()) {
            writeTag(inner);
        }
        //Write Tag_End to signify end of Compound
        input.writeByte(TagType.END.getId());
    }
    
    private void writeListTag(ListTag tag) {
        //Write inner tag-type id
        input.writeByte(tag.getInnerType().getId());
        
        List<NBTTag> innerTags = tag.listTags();
        input.writeInt(innerTags.size());
        for (NBTTag inner : innerTags) {
            writeTag(inner);
        }
    }
    
    private void writeTag(NBTTag tag) {
        input.writeByte(tag.getType().getId());
        
        if (tag.hasName()) {
            writeString(tag.getName());
        }
        
        switch (tag.getType()) {
        case BYTE:
            input.writeByte(tag.asType(ByteTag.class).getValue());
            break;
        case SHORT:
            input.writeShort(tag.asType(ShortTag.class).getValue());
            break;
        case INT:
            input.writeInt(tag.asType(IntTag.class).getValue());
            break;
        case LONG:
            input.writeLong(tag.asType(LongTag.class).getValue());
            break;
        case FLOAT:
            input.writeFloat(tag.asType(FloatTag.class).getValue());
            break;
        case DOUBLE:
            input.writeDouble(tag.asType(DoubleTag.class).getValue());
            break;
        case BYTE_ARRAY:
            byte[] barray = tag.asType(ByteArrayTag.class).getValue();
            input.writeInt(barray.length);
            input.writeBytes(barray);
            break;
        case STRING:
            writeString(tag.asType(StringTag.class).getValue());
            break;
        case LIST:
            writeListTag(tag.asType(ListTag.class));
            break;
        case COMPOUND:
            writeCompoundTag(tag.asType(CompoundTag.class));
            break;
        case INT_ARRAY:
            int[] iarray = tag.asType(IntArrayTag.class).getValue();
            input.writeInt(iarray.length);
            for (int i = 0; i < iarray.length ; i++) {
                input.writeInt(iarray[i]);
            }
            break;
        default:
            //Shouldn't/Can't happen
            break;
        }
    }
    
    private void writeString(String s) {
        input.writeShort(s.length());
        input.writeBytes(s.getBytes(Codec.CHARSET));
    }
}
