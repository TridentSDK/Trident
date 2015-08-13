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
import net.tridentsdk.meta.block.CauldronMeta;
import net.tridentsdk.meta.component.Meta;
import net.tridentsdk.meta.component.MetaCollection;

/**
 * Implementation of the Cauldron Meta.
 *
 * @author The TridentSDK Team.
 */
public class CauldronMetaImpl implements CauldronMeta {
    private short filledLevel;

    @Override
    public short filledLevel() {
        return filledLevel;
    }

    @Override
    public void setFilledLevel(short level) {
        this.filledLevel = level;
        //Is it possible to update this for the players?

    }

    @Override
    public double filledPercentage() {
        return (filledLevel / 3) * 100; //3 is the max Short value for the water level in the cauldron.
    }

    @Override
    public byte encode() {
        return 0;
    }

    @Override
    public Meta<Block> decode(Block instance, float yaw, byte direction, byte cx, byte cy, byte cz, short damageValue) {
        instance.setSubstance(Substance.CAULDRON);
        return this;
    }

    @Override
    public Meta<Block> make() {
        return new CauldronMetaImpl();
    }

    @Override
    public Substance[] applyTo(MetaCollection collection) {
        collection.putIfAbsent(CauldronMeta.class, this);
        return new Substance[] {Substance.CAULDRON};
    }
}
