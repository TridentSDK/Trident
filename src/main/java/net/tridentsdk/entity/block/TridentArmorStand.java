/*
 * Copyright (c) 2014, TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of TridentSDK nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
