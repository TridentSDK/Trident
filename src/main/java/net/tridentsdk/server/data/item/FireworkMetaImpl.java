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
package net.tridentsdk.server.data.item;

import java.util.List;

import net.tridentsdk.meta.item.ExplosionMeta;
import net.tridentsdk.meta.item.FireworkMeta;
import net.tridentsdk.meta.nbt.NBTField;
import net.tridentsdk.meta.nbt.NBTSerializable;
import net.tridentsdk.meta.nbt.TagType;

public class FireworkMetaImpl extends ItemMetaImpl implements FireworkMeta, NBTSerializable {
    @NBTField(name = "Flight", type = TagType.BYTE)
    private byte flight;

    @NBTField(name = "Fireworks", type = TagType.LIST)
    private List<ExplosionMeta> explosions;

    @Override
    public byte flight() {
        return flight;
    }

    @Override
    public List<ExplosionMeta> explosions() {
        return explosions;
    }
}
