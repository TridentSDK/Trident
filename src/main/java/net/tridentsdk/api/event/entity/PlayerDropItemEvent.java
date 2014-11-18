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
package net.tridentsdk.api.event.entity;

import com.google.common.base.Preconditions;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.entity.Item;
import net.tridentsdk.api.entity.living.Player;

public class PlayerDropItemEvent extends EntitySpawnEvent {
    private final Player player;

    public PlayerDropItemEvent(Entity item, Location location, Player player) {
        super(item, location);
        Preconditions.checkArgument(item instanceof Item, "Must drop an item!");
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Item getItem() {
        return (Item) super.getEntity();
    }

    public void setItem(Entity item) {
        super.setEntity(item);
    }
}
