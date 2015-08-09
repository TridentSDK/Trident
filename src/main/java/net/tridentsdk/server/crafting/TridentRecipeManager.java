package net.tridentsdk.server.crafting;

import net.tridentsdk.crafting.CraftTuple;
import net.tridentsdk.crafting.RecipeManager;
import net.tridentsdk.inventory.Item;

import java.util.ArrayList;
import java.util.List;

public class TridentRecipeManager extends RecipeManager {

    private List<SmeltingFuel> smeltingFuels = new ArrayList<>();
    private List<SmeltingRecipe> smeltingRecipes = new ArrayList<>();

    @Override
    public void addShapelessRecipe(Item result, List<Item> source){
        // TODO
    }

    @Override
    public void addShapedRecipe(Item result, char[][] grid, List<CraftTuple> source){
        // TODO
    }

    @Override
    public void addSmeltingRecipe(Item result, Item source, int smeltTicks, float experience){
        smeltingRecipes.add(new SmeltingRecipe(result, source, smeltTicks, experience));
    }

    @Override
    public void addSmeltingFuel(Item source, int burnTicks, Item returnItem){
        smeltingFuels.add(new SmeltingFuel(source, burnTicks, returnItem));
    }

    public boolean isValidSmeltingFuel(Item item){
        for(SmeltingFuel fuel : smeltingFuels){
            if(fuel.source().isSimilarIgnoreQuantity(item)){
                return true;
            }
        }

        return false;
    }

}
