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

import net.tridentsdk.meta.item.SkullMeta;
import net.tridentsdk.meta.nbt.NBTField;
import net.tridentsdk.meta.nbt.NBTSerializable;
import net.tridentsdk.meta.nbt.TagType;

public class SkullMetaImpl extends ItemMetaImpl implements SkullMeta {
    @NBTField(name = "SkullOwner", type = TagType.COMPOUND)
    protected SkullOwnerData data;

    @Override
    public String owner() {
        return data == null ? null : data.name;
    }

    @Override
    public void setOwner(String name) {
        if (data == null) {
            data = new SkullOwnerData();
        }

        data.name = name;
    }

    protected static class SkullOwnerData implements NBTSerializable {
        @NBTField(name = "Name", type = TagType.STRING)
        String name;
        // TODO properties
    }
}
