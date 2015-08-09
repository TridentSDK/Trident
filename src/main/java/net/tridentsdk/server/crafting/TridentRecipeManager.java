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
    public void addShapelessRecipe(Item result, List<Item> source) {
        // TODO
    }

    @Override
    public void addShapedRecipe(Item result, char[][] grid, List<CraftTuple> source) {
        // TODO
    }

    @Override
    public void addSmeltingRecipe(Item result, Item source, int smeltTicks, float experience) {
        smeltingRecipes.add(new SmeltingRecipe(result, source, smeltTicks, experience));
    }

    @Override
    public void addSmeltingFuel(Item source, int burnTicks, Item returnItem) {
        smeltingFuels.add(new SmeltingFuel(source, burnTicks, returnItem));
    }

    public boolean isValidSmeltingFuel(Item item) {
        return smeltingFuels.stream().anyMatch((fuel) -> fuel.source().isSimilarIgnoreQuantity(item));
    }

}
