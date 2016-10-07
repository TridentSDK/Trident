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
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.tridentsdk.base.Enchantment;
import net.tridentsdk.meta.item.ItemDisplayProperties;
import net.tridentsdk.meta.item.ItemMeta;
import net.tridentsdk.meta.nbt.NBTField;
import net.tridentsdk.meta.nbt.TagType;
import net.tridentsdk.util.TridentLogger;

public class ItemMetaImpl implements ItemMeta {
    @NBTField(name = "display", type = TagType.COMPOUND, asClass = ItemDisplayPropertiesImpl.class)
    protected ItemDisplayProperties displayProperties;

    @NBTField(name = "HideFlags", type = TagType.INT)
    protected int flags;

    @NBTField(name = "Enchantments", type = TagType.LIST)
    protected List<EnchantedBookMetaImpl.NBTEnchantment> enchants;

    private Set<HiddenModifierFlag> hiddenFlags = Sets.newConcurrentHashSet();
    private Map<Enchantment, Short> enchantments = Maps.newConcurrentMap();

    @Override
    public void process() {
        for (ItemMeta.HiddenModifierFlag flag : ItemMeta.HiddenModifierFlag.values()) {
            if ((this.flags & flag.modifier()) == flag.modifier()) {
                this.hiddenFlags.add(flag);
            }
        }
        enchants.forEach(e -> {
            Enchantment ench = Enchantment.fromId(e.id);
            if (ench == null) {
                TridentLogger.get().warn("Enchantment found with id=" + e.id + " (not found in Trident)");
            } else {
                enchantments.put(ench, e.lvl);
            }
        });
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

    @Override
    public Map<Enchantment, Short> enchantments() {
        return enchantments;
    }

    @Override
    public void addEnchantment(Enchantment enchantment, int level) {
        checkIntToShort(level);
        enchantments.put(enchantment, (short) level);
    }

    @Override
    public boolean addSafeEnchantment(Enchantment enchantment, int level) {
        checkIntToShort(level);
        /*
         * TODO:
         * if (item().type().canTakeEnchantment(enchantment)) {
         *     addEnchantment(enchantment, level);
         *     return true;
         * } else {
         *     return false;
         * }
         */
        return false;
    }

    @Override
    public void removeEnchantment(Enchantment enchantment) {
        enchantments.remove(enchantment);
    }

    public static void checkIntToShort(int i) {
        if (i < Short.MIN_VALUE || i > Short.MAX_VALUE) {
            throw new IllegalArgumentException("Integer (" + i + ") must be between " + Short.MIN_VALUE + " and " + Short.MAX_VALUE);
        }
    }
}
