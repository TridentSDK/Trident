package net.tridentsdk.server.crafting;

import net.tridentsdk.inventory.Item;

public class SmeltingRecipe {

    private Item result;
    private Item source;
    private int smeltTicks;
    private float experience;

    public SmeltingRecipe(Item result, Item source, int smeltTicks, float experience){
        this.result = result;
        this.source = source;
        this.smeltTicks = smeltTicks;
        this.experience = experience;
    }

    public Item result(){
        return result;
    }

    public Item source(){
        return source;
    }

    public int smeltTicks(){
        return smeltTicks;
    }

    public float experience(){
        return experience;
    }
}
