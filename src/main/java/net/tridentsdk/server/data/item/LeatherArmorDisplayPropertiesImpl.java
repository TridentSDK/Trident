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

import net.tridentsdk.base.Color;
import net.tridentsdk.meta.item.LeatherArmorDisplayProperties;
import net.tridentsdk.meta.nbt.NBTField;
import net.tridentsdk.meta.nbt.TagType;

public class LeatherArmorDisplayPropertiesImpl extends ItemDisplayPropertiesImpl implements LeatherArmorDisplayProperties {
    @NBTField(name = "color", type = TagType.INT)
    protected int color;

    @Override
    public Color color() {
        return Color.fromRGB(color);
    }

    @Override
    public void setColor(Color color) {
        this.color = color.asRGB();
    }
}
