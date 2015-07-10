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

package net.tridentsdk.server.world;

import net.tridentsdk.meta.nbt.NBTField;
import net.tridentsdk.meta.nbt.NBTSerializable;
import net.tridentsdk.meta.nbt.TagType;
import net.tridentsdk.util.NibbleArray;

import java.util.Arrays;

public final class ChunkSection implements NBTSerializable {
    public static final int LENGTH = 4096; // 16^3 (width * height * depth)

    @NBTField(name = "Blocks", type = TagType.BYTE_ARRAY)
    public byte[] rawTypes = new byte[LENGTH];
    @NBTField(name = "Add", type = TagType.BYTE_ARRAY)
    public byte[] add = new byte[LENGTH / 2];
    @NBTField(name = "Data", type = TagType.BYTE_ARRAY)
    public byte[] data = new byte[LENGTH / 2];
    @NBTField(name = "BlockLight", type = TagType.BYTE_ARRAY)
    public byte[] blockLight = new byte[LENGTH / 2];
    @NBTField(name = "SkyLight", type = TagType.BYTE_ARRAY)
    public byte[] skyLight = new byte[LENGTH / 2];
    @NBTField(name = "Y", type = TagType.BYTE)
    protected byte y;
    public char[] types;

    public ChunkSection() {
    }

    /**
     * Gets the position in the section array
     */
    public byte y() {
        return y;
    }

    protected void loadBlocks() {
        //NibbleArray add = new NibbleArray(this.add);
        //NibbleArray data = new NibbleArray(this.data);
        
        // DEBUG ===== makes the entire chunk completely lit, not ideal for production
        Arrays.fill(skyLight, (byte) 255);
        // =====

        types = new char[rawTypes.length];

        for (int i = 0; i < LENGTH; i += 1) {
            byte b;
            byte bData;
            int bAdd;

            /* Get block data; use extras accordingly */
            b = rawTypes[i];
            bAdd = NibbleArray.get(this.add, i) << 12;
            bData = NibbleArray.get(this.data, i);

            types[i] = (char) (bAdd | ((b & 0xff) << 4) | bData);
        }
    }

    protected void updateRaw() {
        updateRaw(types);
    }

    protected void updateRaw(char[] data) {
        if(data.length != LENGTH)
            throw new IllegalArgumentException("Data length must be 4096!");

        for (int i = 0; i < LENGTH; i++) {
            rawTypes[i] = (byte) ((data[i] >> 4) & 0xFF);
            NibbleArray.set(this.data, i, (byte) (data[i] & 0xf));
            NibbleArray.set(this.add, i, (byte) (data[i] >> 12));
        }
    }

    protected void setBlocks(char[] data) {
        this.types = data;
    }

    protected void setData(byte[] data) {
        this.data = data;
    }

    public char[] types() {
        return types;
    }
}