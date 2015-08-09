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
import net.tridentsdk.inventory.InventoryType;
import net.tridentsdk.inventory.Item;
import net.tridentsdk.meta.block.FurnaceMeta;
import net.tridentsdk.meta.component.Meta;
import net.tridentsdk.meta.component.MetaCollection;
import net.tridentsdk.server.inventory.TridentInventory;

public class FurnaceMetaImpl implements FurnaceMeta {

    private TridentInventory furnaceInventory;
    private Item source;
    private Item fuel;
    private Item result;
    private int burnTicks;

    @Override
    public Item sourceSlot(){
        return source;
    }

    @Override
    public Item fuelSlot(){
        return fuel;
    }

    @Override
    public Item resultSlot(){
        return result;
    }

    @Override
    public int burnTicks(){
        return burnTicks;
    }

    @Override
    public void setSourceSlot(Item source){
        this.source = source;
    }

    @Override
    public void setFuelSlot(Item fuel){
        this.fuel = fuel;
    }

    @Override
    public void setResultSlot(Item result){
        this.result = result;
    }

    @Override
    public void setBurnTicks(int burnTicks){
        this.burnTicks = burnTicks;
    }

    @Override
    public byte encode(){
        return 0;
    }

    @Override
    public Meta<Block> decode(Block instance, float yaw, byte direction, byte cx, byte cy, byte cz, short damageValue){
        FurnaceMetaImpl meta = new FurnaceMetaImpl();
        meta.furnaceInventory = TridentInventory.create(null, 3, InventoryType.FURNACE);
        instance.setSubstance(Substance.FURNACE);
        return meta;
    }

    @Override
    public Meta<Block> make(){
        return new FurnaceMetaImpl();
    }

    @Override
    public Substance[] applyTo(MetaCollection collection){
        collection.put(FurnaceMeta.class, this);
        return new Substance[]{Substance.FURNACE};
    }

    public TridentInventory furnaceInventory(){
        return furnaceInventory;
    }

}
