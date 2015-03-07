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

import net.tridentsdk.Position;
import net.tridentsdk.base.Block;
import net.tridentsdk.entity.traits.EntityProperties;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.entity.block.ItemFrame;
import net.tridentsdk.server.entity.TridentEntity;
import net.tridentsdk.window.inventory.Item;

import java.util.UUID;

public class TridentItemFrame extends TridentEntity implements ItemFrame {
    public TridentItemFrame(UUID id, Position spawnLocation) {
        super(id, spawnLocation);
    }

    @Override
    public Item item() {
        return null;
    }

    @Override
    public byte itemRotation() {
        return 0;
    }

    @Override
    public Block hangingBlock() {
        return null;
    }

    @Override
    public EntityType type() {
        return EntityType.ITEM_FRAME;
    }
}
