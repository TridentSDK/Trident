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

import net.tridentsdk.effect.potion.PotionEffect;
import net.tridentsdk.meta.item.PotionMeta;
import net.tridentsdk.meta.nbt.NBTField;
import net.tridentsdk.meta.nbt.TagType;
import net.tridentsdk.server.effect.potion.TridentPotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PotionMetaImpl extends ItemMetaImpl implements PotionMeta {
    @NBTField(name = "CustomPotionEffects", type = TagType.LIST)
    protected List<TridentPotionEffect> additionalEffects = new ArrayList<>();

    @Override
    public List<PotionEffect> effects() {
        return additionalEffects.stream().map((e) -> e).collect(Collectors.toList());
    }

    @Override
    public void setEffects(List<PotionEffect> effects) {
        this.additionalEffects = effects.stream()
                .map((e) -> (TridentPotionEffect) e)
                .collect(Collectors.toList());
    }
}
