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
import java.util.stream.Collectors;

import net.tridentsdk.meta.item.MapDecoration;
import net.tridentsdk.meta.item.MapMeta;
import net.tridentsdk.meta.nbt.NBTField;
import net.tridentsdk.meta.nbt.NBTSerializable;
import net.tridentsdk.meta.nbt.TagType;

public class MapMetaImpl extends ItemMetaImpl implements MapMeta, NBTSerializable {
    @NBTField(name = "map_is_scaling", type = TagType.BYTE)
    private boolean scaling;

    @NBTField(name = "Decorations", type = TagType.LIST)
    private List<MapDecorationImpl> decorations;

    @Override
    public boolean isScaling() {
        return scaling;
    }

    @Override
    public List<MapDecoration> decorations() {
        return decorations.stream().collect(Collectors.toList());
    }
}
