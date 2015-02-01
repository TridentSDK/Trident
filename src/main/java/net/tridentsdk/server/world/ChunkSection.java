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

import net.tridentsdk.Coordinates;
import net.tridentsdk.base.Block;
import net.tridentsdk.base.Substance;
import net.tridentsdk.meta.nbt.NBTField;
import net.tridentsdk.meta.nbt.NBTSerializable;
import net.tridentsdk.meta.nbt.TagType;
import net.tridentsdk.util.NibbleArray;
import net.tridentsdk.world.World;

import java.util.Arrays;

public final class ChunkSection implements NBTSerializable {
    static final Coordinates DUMMY_COORDS = Coordinates.create(null, 0, 0, 0);
    static final int LENGTH = 4096; // 16^3 (width * height * depth)

    @NBTField(name = "Blocks", type = TagType.BYTE_ARRAY)
    public byte[] rawTypes;
    @NBTField(name = "Add", type = TagType.BYTE_ARRAY)
    public byte[] add;
    @NBTField(name = "Data", type = TagType.BYTE_ARRAY)
    public byte[] data;
    @NBTField(name = "BlockLight", type = TagType.BYTE_ARRAY)
    public byte[] blockLight;
    @NBTField(name = "BlockLight", type = TagType.BYTE_ARRAY)
    public byte[] skyLight;
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
        if (add == null) {
            add = new byte[LENGTH];
        }

        //NibbleArray add = new NibbleArray(this.add);
        //NibbleArray data = new NibbleArray(this.data);

        Arrays.fill(skyLight, (byte) 15);

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

    public char[] types() {
        return types;
    }
}