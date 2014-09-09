/*
 * Copyright (c) 2014, The TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     1. Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *     3. Neither the name of the The TridentSDK Team nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL The TridentSDK Team BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.tridentsdk.data;

import net.tridentsdk.api.nbt.ByteArrayTag;
import net.tridentsdk.api.nbt.ByteTag;
import net.tridentsdk.api.nbt.CompoundTag;
import net.tridentsdk.api.nbt.DoubleTag;
import net.tridentsdk.api.nbt.FloatTag;
import net.tridentsdk.api.nbt.IntArrayTag;
import net.tridentsdk.api.nbt.IntTag;
import net.tridentsdk.api.nbt.ListTag;
import net.tridentsdk.api.nbt.LongTag;
import net.tridentsdk.api.nbt.NBTException;
import net.tridentsdk.api.nbt.NBTTag;
import net.tridentsdk.api.nbt.NullTag;
import net.tridentsdk.api.nbt.ShortTag;
import net.tridentsdk.api.nbt.StringTag;
import net.tridentsdk.api.nbt.TagType;
import net.tridentsdk.server.netty.Codec;
import io.netty.buffer.ByteBuf;

/**
 * @author The TridentSDK Team
 */
public class NBTByteBufBuilder {
    ByteBuf input;
    
    public NBTByteBufBuilder() { };
    
    public NBTByteBufBuilder input(ByteBuf input) {
        this.input = input;
        return this;
    }
    
    public CompoundTag build() throws NBTException {
        TagType initType = TagType.fromId(input.readByte());
        
        //NBT source must start with a compound tag or is invalid
        if (!initType.equals(TagType.COMPOUND)) {
            throw new NBTException("NBT Data must start with a Compound Tag.");
        }
        
        //Create the resulting CompoundTag to return
        //Uses recursion to recursively walk through the tag tree
        CompoundTag result = resolveCompoundTag(readString());
        
        return result;
    }
    
    private CompoundTag resolveCompoundTag(String name) {
        CompoundTag compound = new CompoundTag(name);
        
        TagType innerType;
        while (!(innerType = TagType.fromId(input.readByte())).equals(TagType.END)) {
            compound.addTag(resolveTag(innerType, true));
        }
        
        return compound;
    }
    
    private ListTag resolveListTag(String name) {
        TagType listType = TagType.fromId(input.readByte());
        
        ListTag list = new ListTag(name, listType);
        
        int length = input.readInt();
        
        for (int i = 0; i < length; i++) {
            list.addTag(resolveTag(listType, false));
        }
        
        return list;
        
    }
    
    private NBTTag resolveTag(TagType type, boolean withName) {
        NBTTag result = null;
        
        //Reads name if required
        String name = null;
        if (withName) {
            name = readString();
        }
        
        switch (type) {
        case BYTE:
            result = new ByteTag(name);
            result.asType(ByteTag.class).setValue(input.readByte());
            break;
        case SHORT:
            result = new ShortTag(name);
            result.asType(ShortTag.class).setValue(input.readShort());
            break;
        case INT:
            result = new IntTag(name);
            result.asType(IntTag.class).setValue(input.readInt());
            break;
        case LONG:
            result = new LongTag(name);
            result.asType(LongTag.class).setValue(input.readLong());
            break;
        case FLOAT:
            result = new FloatTag(name);
            result.asType(FloatTag.class).setValue(input.readFloat());
            break;
        case DOUBLE:
            result = new DoubleTag(name);
            result.asType(DoubleTag.class).setValue(input.readDouble());
            break;
        case BYTE_ARRAY:
            result = new ByteArrayTag(name);
            int balength = input.readInt();
            byte[] babytes = new byte[balength];
            input.readBytes(babytes);
            result.asType(ByteArrayTag.class).setValue(babytes);
            break;
        case STRING:
            result = new StringTag(name);
            result.asType(StringTag.class).setValue(readString());
            break;
        case LIST:
            result = resolveListTag(name);
            break;
        case COMPOUND:
            result = resolveCompoundTag(name);
            break;
        case INT_ARRAY:
            result = new IntArrayTag(name);
            int ialength = input.readInt();
            int[] array = new int[ialength];
            for (int i = 0; i < array.length ; i++) {
                array[i] = input.readInt();
            }
            result.asType(IntArrayTag.class).setValue(array);
            break;
        default:
            result = new NullTag(name);
            break;
        }

        return result;
    }
    
    private String readString() {
        short length = input.readShort();
        byte[] bytes = new byte[length];
        input.readBytes(bytes);
        
        return new String(bytes, Codec.CHARSET);
    }

}
