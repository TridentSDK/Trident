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
import java.util.Map;

import com.google.common.collect.Maps;

import net.tridentsdk.base.Enchantment;
import net.tridentsdk.meta.item.EnchantedBookMeta;
import net.tridentsdk.meta.nbt.NBTField;
import net.tridentsdk.meta.nbt.NBTSerializable;
import net.tridentsdk.meta.nbt.TagType;

public class EnchantedBookMetaImpl implements EnchantedBookMeta, NBTSerializable {
    @NBTField(name = "StoredEnchantments", type = TagType.LIST)
    private List<NBTEnchantment> enchants;

    @NBTField(name = "RepairCost", type = TagType.STRING)
    private int repairCost = 0;

    private Map<Enchantment, Short> enchantments = Maps.newConcurrentMap();

    @Override
    public void process() {
        enchants.forEach(e -> enchantments.put(Enchantment.fromId(e.id), e.lvl));
    }

    @Override
    public Map<Enchantment, Short> storedEnchantments() {
        return this.enchantments;
    }

    @Override
    public int repairCost() {
        return this.repairCost;
    }

    protected static class NBTEnchantment implements NBTSerializable {
        @NBTField(name = "id", type = TagType.SHORT)
        short id;

        @NBTField(name = "lvl", type = TagType.SHORT)
        short lvl;
    }
}
