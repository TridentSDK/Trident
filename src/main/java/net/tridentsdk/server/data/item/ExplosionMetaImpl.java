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

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import net.tridentsdk.base.Color;
import net.tridentsdk.meta.item.ExplosionMeta;
import net.tridentsdk.meta.nbt.NBTField;
import net.tridentsdk.meta.nbt.NBTSerializable;
import net.tridentsdk.meta.nbt.TagType;

public class ExplosionMetaImpl implements ExplosionMeta, NBTSerializable {
    @NBTField(name = "Colors", type = TagType.INT_ARRAY)
    private int[] mainColorsArr;

    @NBTField(name = "FadeColors", type = TagType.INT_ARRAY)
    private int[] fadeColorsArr;

    @NBTField(name = "Flicker", type = TagType.BYTE)
    private boolean flicker;

    @NBTField(name = "Trail", type = TagType.BYTE)
    private boolean trail;

    @NBTField(name = "Type", type = TagType.BYTE)
    private int type;

    private ExplosionType explosionType;
    private List<Color> mainColors;
    private List<Color> fadeColors;

    @Override
    public void process() {
        if (type < 0 || type >= ExplosionMeta.ExplosionType.values().length) {
            explosionType = ExplosionMeta.ExplosionType.UNKNOWN;
        } else {
            explosionType = ExplosionMeta.ExplosionType.values()[type];
        }

        mainColors = Lists.newArrayListWithCapacity(mainColorsArr.length);
        fadeColors = Lists.newArrayListWithCapacity(fadeColorsArr.length);

        Arrays.stream(mainColorsArr).mapToObj((i) -> Color.fromRGB(i)).forEach(c -> mainColors.add(c));
        Arrays.stream(fadeColorsArr).mapToObj((i) -> Color.fromRGB(i)).forEach(c -> fadeColors.add(c));
    }

    @Override
    public List<Color> colors() {
        return mainColors;
    }

    @Override
    public List<Color> fadeColors() {
        return fadeColors;
    }

    @Override
    public ExplosionType type() {
        return explosionType;
    }

    @Override
    public boolean isFlicker() {
        return flicker;
    }

    @Override
    public boolean isTrail() {
        return trail;
    }
}
