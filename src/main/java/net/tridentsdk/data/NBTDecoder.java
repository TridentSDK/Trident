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

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.nbt.*;
import net.tridentsdk.server.netty.Codec;

/**
 * @author The TridentSDK Team
 */
public class NBTDecoder {
    final ByteBuf input;

    public NBTDecoder(ByteBuf input) {
        this.input = input;
    }

    public CompoundTag decode() throws NBTException {
        return this.decode(this.input.readByte());
    }

    public CompoundTag decode(byte b) throws NBTException {
        TagType initType = TagType.fromId(b);

        //NBT source must start with a compound tag or is invalid
        if (initType != TagType.COMPOUND) {
            throw new NBTException("NBT Data must start with a Compound Tag.");
        }

        //Create the resulting CompoundTag to return
        //Uses recursion to recursively walk through the tag tree
        return this.resolveCompoundTag(this.readString());
    }

    private CompoundTag resolveCompoundTag(String name) {
        CompoundTag compound = new CompoundTag(name);
        TagType innerType;

        while ((innerType = TagType.fromId(input.readByte())) != TagType.END) {
            compound.addTag(this.resolveTag(innerType, true));
        }

        return compound;
    }

    private ListTag resolveListTag(String name) {
        TagType listType = TagType.fromId(this.input.readByte());
        ListTag list = new ListTag(name, listType);
        int length = this.input.readInt();

        for (int i = 0; i < length; i++) {
            list.addTag(this.resolveTag(listType, false));
        }

        return list;
    }

    private NBTTag resolveTag(TagType type, boolean withName) {

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

                this.input.readBytes(babytes);
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

    private String readString() {
        short length = this.input.readShort();
        byte[] bytes = new byte[(int) length];

        this.input.readBytes(bytes);

        return new String(bytes, Codec.CHARSET);
    }
}
