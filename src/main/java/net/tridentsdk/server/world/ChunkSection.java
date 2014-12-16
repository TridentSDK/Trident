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
import net.tridentsdk.base.Substance;
import net.tridentsdk.base.Tile;
import net.tridentsdk.meta.nbt.NBTField;
import net.tridentsdk.meta.nbt.NBTSerializable;
import net.tridentsdk.meta.nbt.TagType;
import net.tridentsdk.util.NibbleArray;
import net.tridentsdk.world.World;

final class ChunkSection implements NBTSerializable {
    static final int LENGTH = 4096; // 16^3 (width * height * depth)

    @NBTField(name = "Blocks", type = TagType.BYTE_ARRAY)
    protected byte[] rawTypes;

    @NBTField(name = "Add", type = TagType.BYTE_ARRAY)
    protected byte[] add;

    @NBTField(name = "Data", type = TagType.BYTE_ARRAY)
    protected byte[] data;

    @NBTField(name = "BlockLight", type = TagType.BYTE_ARRAY)
    protected byte[] blockLight;

    @NBTField(name = "BlockLight", type = TagType.BYTE_ARRAY)
    protected byte[] skyLight;

    private final Tile[] blcks = new Tile[LENGTH];
    private byte[] types;

    protected ChunkSection() {}

    protected void loadBlocks(World world) {
        if(add == null) {
            add = new byte[LENGTH];
        }

        NibbleArray add = new NibbleArray(this.add);
        NibbleArray data = new NibbleArray(this.data);

        types = new byte[rawTypes.length];

        for (int i = 0; i < LENGTH; i += 1) {
            Tile block;
            byte b;
            byte bData;
            int bAdd;

            /* Get block data; use extras accordingly */
            b = rawTypes[i];
            bAdd = add.get(i) << 8;
            b += bAdd;
            bData = data.get(i);

            Substance material = Substance.fromString(String.valueOf(b));

            if(material == null) {
                material = Substance.AIR; // check if valid
            }

            block = new TridentTile(new Coordinates(world, 0, 0, 0), material, bData); // TODO: get none-relative location

                /* TODO get the type and deal with block data accordingly */
            switch (block.getSubstance()) {
                default:
                    break;
            }

            blcks[i] = block;
            types[i] = (byte) (bAdd | ((b & 0xff) << 4) | bData);
        }
    }

    public byte[] getTypes() {
        return types;
    }

    public Tile[] getBlocks() {
        return blcks;
    }
}