/*
 *     Trident - A Multithreaded Server Alternative
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
package net.tridentsdk.entity.block;

import net.tridentsdk.api.Location;
import net.tridentsdk.api.entity.EntityProperties;
import net.tridentsdk.api.entity.SlotProperties;
import net.tridentsdk.api.entity.block.ArmorStand;
import net.tridentsdk.api.inventory.ItemStack;
import net.tridentsdk.api.util.PartRotation;
import net.tridentsdk.entity.TridentEntity;

import java.util.UUID;

public class TridentArmorStand extends TridentEntity implements ArmorStand {

    public TridentArmorStand(UUID id, Location spawnLocation) {
        super(id, spawnLocation);
    }

    @Override
    public SlotProperties getSlotProperties() {
        return null;
    }

    @Override
    public boolean isInvisible() {
        return false;
    }

    @Override
    public boolean displayBaseplate() {
        return false;
    }

    @Override
    public boolean displayArms() {
        return false;
    }

    @Override
    public boolean useGravity() {
        return false;
    }

    @Override
    public PartRotation[] getPose() {
        return new PartRotation[0];
    }

    @Override
    public boolean isTiny() {
        return false;
    }

    @Override
    public ItemStack[] getEquipment() {
        return new ItemStack[0];
    }

    @Override
    public boolean isNameVisible() {
        return false;
    }

    @Override
    public void applyProperties(EntityProperties properties) {

    }
}
