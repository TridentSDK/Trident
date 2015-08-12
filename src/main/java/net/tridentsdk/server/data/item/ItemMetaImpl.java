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

import java.util.Set;

import com.google.common.collect.Sets;

import net.tridentsdk.meta.item.ItemDisplayProperties;
import net.tridentsdk.meta.item.ItemMeta;
import net.tridentsdk.meta.nbt.NBTField;
import net.tridentsdk.meta.nbt.TagType;

public class ItemMetaImpl implements ItemMeta {
    @NBTField(name = "display", type = TagType.COMPOUND, asClass = ItemDisplayPropertiesImpl.class)
    protected ItemDisplayProperties displayProperties;

    @NBTField(name = "HideFlags", type = TagType.INT)
    protected int flags;

    Set<HiddenModifierFlag> hiddenFlags = Sets.newConcurrentHashSet();

    @Override
    public void process() {
        for (ItemMeta.HiddenModifierFlag flag : ItemMeta.HiddenModifierFlag.values()) {
            if ((this.flags & flag.modifier()) == flag.modifier()) {
                this.hiddenFlags.add(flag);
            }
        }
    }

    @Override
    public ItemDisplayProperties displayProperties() {
        return this.displayProperties;
    }

    @Override
    public void setDisplayProperties(ItemDisplayProperties properties) {
        this.displayProperties = properties;
    }

    @Override
    public Set<HiddenModifierFlag> flags() {
        return this.hiddenFlags;
    }

    @Override
    public void setFlag(HiddenModifierFlag flag, boolean shown) {
        if (shown) {
            if (this.hiddenFlags.contains(flag)) {
                this.hiddenFlags.remove(flag);
                this.flags -= flag.modifier();
            }
        } else {
            if (this.hiddenFlags.contains(flag)) {
                this.hiddenFlags.add(flag);
                this.flags += flag.modifier();
            }
        }
    }
}
