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

public class SmeltingFuel {

    private Item source;
    private int burnTicks;
    private Item returnItem;

    public SmeltingFuel(Item source, int burnTicks, Item returnItem) {
        this.source = source;
        this.burnTicks = burnTicks;
        this.returnItem = returnItem;
    }

    public Item source() {
        return source;
    }

    public int burnTicks() {
        return burnTicks;
    }

    public Item returnItem() {
        return returnItem;
    }

}
