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
package net.tridentsdk.impl.entity.block;

import net.tridentsdk.api.Block;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.entity.EntityProperties;
import net.tridentsdk.api.entity.block.ItemFrame;
import net.tridentsdk.api.inventory.ItemStack;
import net.tridentsdk.impl.entity.TridentEntity;

import java.util.UUID;

public class TridentItemFrame extends TridentEntity implements ItemFrame {

    public TridentItemFrame(UUID id, Location spawnLocation) {
        super(id, spawnLocation);
    }

    @Override
    public ItemStack getCurrentItem() {
        return null;
    }

    @Override
    public byte getItemStackRotation() {
        return (byte) 0;
    }

    @Override
    public Block getBlockPlacedOn() {
        return null;
    }

    @Override
    public boolean isNameVisible() {
        return false;
    }

    @Override
    public void applyProperties(EntityProperties properties) {

    }
}
