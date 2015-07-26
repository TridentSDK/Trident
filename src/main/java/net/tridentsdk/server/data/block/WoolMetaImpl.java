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
package net.tridentsdk.server.data.block;

import net.tridentsdk.base.Block;
import net.tridentsdk.base.Substance;
import net.tridentsdk.base.SubstanceColor;
import net.tridentsdk.meta.block.WoolMeta;
import net.tridentsdk.meta.component.Meta;
import net.tridentsdk.meta.component.MetaCollection;

/**
 * Represents data held by a wool block
 *
 * @author The TridentSDK Team
 */
public class WoolMetaImpl implements WoolMeta {
    private volatile SubstanceColor color = SubstanceColor.WHITE;

    @Override
    public void setColor(SubstanceColor color) {
        this.color = color;
    }

    @Override
    public SubstanceColor color() {
        return color;
    }

    @Override
    public Substance[] applyTo(MetaCollection collection) {
        collection.put(WoolMeta.class, this);
        return new Substance[]{Substance.WOOL};
    }

    @Override
    public Meta<Block> decode(Block instance, float yaw, byte direction, byte cx, byte cy, byte cz, short damageValue) {
        WoolMeta meta = new WoolMetaImpl();
        meta.setColor(SubstanceColor.of((byte) damageValue));
        return meta;
    }

    @Override
    public byte encode() {
        return (byte) color.asInt();
    }

    @Override
    public Meta<Block> make() {
        return new WoolMetaImpl();
    }
}