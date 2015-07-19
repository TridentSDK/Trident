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

package net.tridentsdk.server.entity.living;

import net.tridentsdk.Position;
import net.tridentsdk.base.BlockSnapshot;
import net.tridentsdk.base.Substance;
import net.tridentsdk.entity.living.Enderman;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.event.entity.EntityDamageEvent;
import net.tridentsdk.meta.nbt.CompoundTag;
import net.tridentsdk.meta.nbt.ShortTag;
import net.tridentsdk.server.data.MetadataType;
import net.tridentsdk.server.data.ProtocolMetadata;
import net.tridentsdk.server.entity.TridentLivingEntity;

import java.util.UUID;

public class TridentEnderman extends TridentLivingEntity implements Enderman {
    private volatile BlockSnapshot carryingBlock;
    private volatile boolean hostile = false;

    public TridentEnderman(UUID id, Position spawnLocation) {
        super(id, spawnLocation);
    }

    @Override
    public void doLoad(CompoundTag tag) {
        short carriedId = ((ShortTag) tag.getTag("carried")).value();

        if(carriedId != 0) {
            carryingBlock = BlockSnapshot.from(null, Substance.fromId((byte) carriedId),
                    (byte) ((ShortTag) tag.getTag("carriedData")).value());
        }
    }

    @Override
    protected void doEncodeMeta(ProtocolMetadata protocolMeta) {
        protocolMeta.setMeta(16, MetadataType.SHORT, (short) carryingBlock.type().id());
        protocolMeta.setMeta(17, MetadataType.BYTE, carryingBlock.meta());
        protocolMeta.setMeta(18, MetadataType.BYTE, hostile ? (byte) 1 : (byte) 0);
    }

    @Override
    public BlockSnapshot carryingBlock() {
        return carryingBlock;
    }

    @Override
    public int endermiteCount() {
        return 0;
    }

    @Override
    public boolean isHostile() {
        return hostile;
    }

    @Override
    public EntityDamageEvent lastDamageEvent() {
        return null;
    }

    @Override
    public Player lastPlayerDamager() {
        return null;
    }

    @Override
    public EntityType type() {
        return EntityType.ENDERMAN;
    }
}
