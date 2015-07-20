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

package net.tridentsdk.server.window;

import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.ImmutableList;
import net.tridentsdk.inventory.Inventories;
import net.tridentsdk.inventory.Inventory;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the inventory windows on the server, whether being viewed or not
 *
 * @author The TridentSDK Team
 */
@ThreadSafe
public class TridentInventories extends ForwardingCollection<Inventory> implements Inventories {
    private static final Map<Integer, TridentInventory> windows = new ConcurrentHashMap<>();

    @Override
    public Inventory fromId(int id) {
        return windows.get(id);
    }

    @Override
    public void register(Inventory window) {
        windows.put(window.id(), (TridentInventory) window);
    }

    @Override
    protected Collection<Inventory> delegate() {
        return ImmutableList.copyOf(windows.values());
    }
}
