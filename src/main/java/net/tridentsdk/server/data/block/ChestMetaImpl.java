package net.tridentsdk.server.data.block;

import net.tridentsdk.base.Block;
import net.tridentsdk.base.Substance;
import net.tridentsdk.inventory.Inventory;
import net.tridentsdk.inventory.InventoryType;
import net.tridentsdk.meta.block.ChestMeta;
import net.tridentsdk.meta.component.Meta;
import net.tridentsdk.meta.component.MetaCollection;
import net.tridentsdk.server.inventory.TridentInventory;

public class ChestMetaImpl implements ChestMeta {

    private TridentInventory inventory;

    @Override
    public Inventory inventory(){
        return inventory;
    }

    @Override
    public byte encode(){
        return 0;
    }

    @Override
    public Meta<Block> decode(Block instance, float yaw, byte direction, byte cx, byte cy, byte cz, short damageValue){
        ChestMetaImpl meta = new ChestMetaImpl();
        meta.inventory = TridentInventory.create(null, 27, InventoryType.CHEST);
        instance.setSubstance(Substance.CHEST);
        return meta;
    }

    @Override
    public Meta<Block> make(){
        return new ChestMetaImpl();
    }

    @Override
    public Substance[] applyTo(MetaCollection collection){
        collection.put(ChestMeta.class, this);
        return new Substance[]{Substance.CHEST};
    }

}
