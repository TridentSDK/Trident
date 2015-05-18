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
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.entity.vehicle.TNTMinecart;

import java.util.UUID;

/**
 * Represents a minecart that holds a TNT block
 *
 * @author The TridentSDK Team
 */
public class TridentTntMinecart extends TridentMinecart implements TNTMinecart {
    public TridentTntMinecart(UUID uuid, Position spawnPosition) {
        super(uuid, spawnPosition);
    }

    @Override
    public int fuseTime() {
        return 0;
    }

    @Override
    public void setFuseTime(int time) {

    }

    @Override
    public EntityType type() {
        return EntityType.TNT_MINECART;
    }
}
