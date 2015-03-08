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
import net.tridentsdk.entity.traits.Ageable;
import net.tridentsdk.server.data.MetadataType;
import net.tridentsdk.server.data.ProtocolMetadata;

import java.util.UUID;

public abstract class TridentAgeable extends TridentLivingEntity implements Ageable {
    protected volatile int age;

    public TridentAgeable(UUID id, Position spawnLocation) {
        super(id, spawnLocation);
    }

    @Override
    protected void encodeMetadata(ProtocolMetadata protocolMeta) {
        super.encodeMetadata(protocolMeta);

        protocolMeta.setMeta(12, MetadataType.BYTE, age);
    }

    @Override
    public void setAge(int ticks) {
        this.age = ticks;
    }

    @Override
    public int age() {
        return age;
    }
}
