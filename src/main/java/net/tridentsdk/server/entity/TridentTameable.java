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

import net.tridentsdk.base.Position;
import net.tridentsdk.entity.traits.Tameable;
import net.tridentsdk.server.data.MetadataType;
import net.tridentsdk.server.data.ProtocolMetadata;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.util.TridentLogger;

import java.util.UUID;

public abstract class TridentTameable extends TridentBreedable implements Tameable {

    protected volatile byte tameData;
    protected volatile UUID owner;

    protected TridentTameable(UUID id, Position spawnLocation) {
        super(id, spawnLocation);

        this.tameData = 0;
    }

    @Override
    protected void doEncodeMeta(ProtocolMetadata protocolMeta) {
        protocolMeta.setMeta(16, MetadataType.BYTE, tameData);
        protocolMeta.setMeta(17, MetadataType.STRING,
                owner == null ? "" : TridentPlayer.getPlayer(owner).name());
    }

    @Override
    public boolean isSitting() {
        return (tameData & 1) == 1;
    }

    @Override
    public UUID owner() {
        return owner;
    }

    @Override
    public boolean isTamed() {
        return (tameData & 4) == 4;
    }

    public void setTame(final UUID owner) {
        if(TridentPlayer.getPlayer(owner) == null) {
            TridentLogger.get().error(new IllegalArgumentException("No player found with provided UUID!"));
            return;
        }

        TridentTameable.this.owner = owner;
        tameData |= 4;
    }
}
