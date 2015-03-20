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
import net.tridentsdk.entity.living.Creeper;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.event.entity.EntityDamageEvent;
import net.tridentsdk.meta.nbt.ByteTag;
import net.tridentsdk.meta.nbt.CompoundTag;
import net.tridentsdk.meta.nbt.FloatTag;
import net.tridentsdk.meta.nbt.ShortTag;
import net.tridentsdk.server.data.MetadataType;
import net.tridentsdk.server.data.ProtocolMetadata;
import net.tridentsdk.server.entity.TridentLivingEntity;

import java.util.UUID;

public class TridentCreeper extends TridentLivingEntity implements Creeper {
    private volatile boolean charged;
    private volatile float explosionRadius;
    private volatile short fuse;
    private volatile boolean ignited;

    public TridentCreeper(UUID id, Position spawnLocation) {
        super(id, spawnLocation);
    }

    @Override
    public void doLoad(CompoundTag tag) {
        if (tag.containsTag("powered")) {
            this.charged = ((ByteTag) tag.getTag("powered")).value() == 1;
        }

        this.explosionRadius = ((FloatTag) tag.getTag("ExplosionRadius")).value();
        this.fuse = ((ShortTag) tag.getTag("Fuse")).value();
        this.ignited = ((ByteTag) tag.getTag("ignited")).value() == 1;
    }

    @Override
    protected void doEncodeMeta(ProtocolMetadata protocolMeta) {
        protocolMeta.setMeta(16, MetadataType.BYTE, ignited ? 1 : -1);
        protocolMeta.setMeta(17, MetadataType.BYTE, charged ? 1 : 0);
    }

    @Override
    public boolean isElectric() {
        return charged;
    }

    @Override
    public void setElectric(boolean powered) {
        this.charged = powered;
    }

    @Override
    public float explosionRadius() {
        return explosionRadius;
    }

    @Override
    public void setExplosionRadius(float rad) {
        this.explosionRadius = rad;
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
        return EntityType.CREEPER;
    }
}
