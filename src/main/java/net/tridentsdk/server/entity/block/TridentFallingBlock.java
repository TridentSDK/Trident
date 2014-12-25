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

import net.tridentsdk.Coordinates;
import net.tridentsdk.base.TileSnapshot;
import net.tridentsdk.entity.EntityProperties;
import net.tridentsdk.entity.block.FallingBlock;
import net.tridentsdk.server.entity.TridentEntity;

import java.util.UUID;

public class TridentFallingBlock extends TridentEntity implements FallingBlock {
    public TridentFallingBlock(UUID id, Coordinates spawnLocation) {
        super(id, spawnLocation);
    }

    @Override
    public TileSnapshot getSnapshot() {
        return null;
    }

    @Override
    public boolean shouldDrop() {
        return false;
    }

    @Override
    public boolean isNameVisible() {
        return false;
    }

    @Override
    public void applyProperties(EntityProperties properties) {

    }
}
