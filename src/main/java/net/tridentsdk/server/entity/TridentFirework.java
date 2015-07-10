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
package net.tridentsdk.server.entity;

import net.tridentsdk.Position;
import net.tridentsdk.entity.Firework;
import net.tridentsdk.entity.traits.FireworkProperties;
import net.tridentsdk.entity.types.EntityType;

import java.util.UUID;

/**
 * Represents a firable firework that explodes after a set amount of time
 *
 * @author The TridentSDK Team
 */
public class TridentFirework extends TridentEntity implements Firework {
    public TridentFirework(UUID uuid, Position spawnPosition) {
        super(uuid, spawnPosition);
    }

    @Override
    public int timeLived() {
        return 0;
    }

    @Override
    public FireworkProperties properties() {
        return null;
    }

    @Override
    public EntityType type() {
        return EntityType.FIREWORK;
    }
}
