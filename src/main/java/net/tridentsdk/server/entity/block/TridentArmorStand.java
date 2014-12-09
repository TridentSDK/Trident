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
package net.tridentsdk.server.entity.block;

import net.tridentsdk.entity.EntityProperties;
import net.tridentsdk.entity.block.ArmorStand;
import net.tridentsdk.entity.block.SlotProperties;
import net.tridentsdk.server.entity.TridentEntity;
import net.tridentsdk.util.PartRotation;

import java.util.UUID;

public class TridentArmorStand extends TridentEntity implements ArmorStand {
    public TridentArmorStand(UUID id, Coordinates spawnLocation) {
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
    public void setEquipment(ItemStack[] stack) {
    }

    @Override
    public boolean isNameVisible() {
        return false;
    }

    @Override
    public void applyProperties(EntityProperties properties) {

    }
}
