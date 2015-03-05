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
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.EntityType;
import net.tridentsdk.entity.living.Pig;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.event.entity.EntityDamageEvent;
import net.tridentsdk.server.data.MetadataType;
import net.tridentsdk.server.data.ProtocolMetadata;
import net.tridentsdk.server.entity.TridentAgeable;

import java.util.UUID;

public class TridentPig extends TridentAgeable implements Pig {
    protected volatile boolean hasSaddle;

    public TridentPig(UUID id, Position spawnLocation) {
        super(id, spawnLocation);
        hasSaddle = false;
    }

    @Override
    protected void encodeMetadata(ProtocolMetadata protocolMeta) {
        super.encodeMetadata(protocolMeta);

        protocolMeta.setMeta(16, MetadataType.BYTE, (hasSaddle) ? (byte) 1 : (byte) 0);
    }

    @Override
    public boolean canBreed() {
        return false;
    }

    @Override
    public boolean isInLove() {
        return false;
    }

    @Override
    public boolean isSaddled() {
        return hasSaddle;
    }

    @Override
    public void setSaddled(boolean saddled) {
        this.executor.execute(() -> hasSaddle = saddled);
    }

    @Override
    public void hide(Entity entity) {

    }

    @Override
    public void show(Entity entity) {

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
        return EntityType.PIG;
    }
}
