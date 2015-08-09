package net.tridentsdk.server.crafting;

import net.tridentsdk.inventory.Item;

public class SmeltingFuel {

    private Item source;
    private int burnTicks;
    private Item returnItem;

    public SmeltingFuel(Item source, int burnTicks, Item returnItem){
        this.source = source;
        this.burnTicks = burnTicks;
        this.returnItem = returnItem;
    }

    public Item source(){
        return source;
    }

    public int burnTicks(){
        return burnTicks;
    }

    public Item returnItem(){
        return returnItem;
    }

}
