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
package net.tridentsdk.server.entity.vehicle;

import net.tridentsdk.Position;
import net.tridentsdk.base.BlockSnapshot;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.entity.vehicle.CommandMinecart;

import java.util.UUID;

/**
 * Represents a minecart that holds a command block
 *
 * @author The TridentSDK Team
 */
public class TridentCmdMinecart extends TridentMinecart implements CommandMinecart {
    public TridentCmdMinecart(UUID uuid, Position spawnPosition) {
        super(uuid, spawnPosition);
    }

    @Override
    public BlockSnapshot getCommandBlock() {
        return null;
    }

    @Override
    public EntityType getType() {
        return EntityType.COMMAND_MINECART;
    }
}
