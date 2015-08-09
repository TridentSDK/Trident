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
