/*
 *     TridentSDK - A Minecraft Server API
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.api.event.entity;

import net.tridentsdk.api.Location;
import net.tridentsdk.api.entity.Entity;
import net.tridentsdk.api.entity.Item;
import net.tridentsdk.api.entity.living.Player;
import org.apache.commons.lang.Validate;

public class PlayerDropItemEvent extends EntitySpawnEvent {
    private final Player player;

    public PlayerDropItemEvent(Entity item, Location location, Player player) {
        super(item, location);
        Validate.isTrue(item instanceof Item, "Must drop an item!");
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
