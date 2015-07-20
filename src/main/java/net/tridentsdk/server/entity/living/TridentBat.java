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

import net.tridentsdk.base.Position;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.Projectile;
import net.tridentsdk.entity.living.Bat;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.traits.EntityProperties;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.event.entity.EntityDamageEvent;
import net.tridentsdk.meta.nbt.ByteTag;
import net.tridentsdk.meta.nbt.CompoundTag;
import net.tridentsdk.server.data.MetadataType;
import net.tridentsdk.server.data.ProtocolMetadata;
import net.tridentsdk.server.entity.TridentLivingEntity;

import java.util.UUID;

public class TridentBat extends TridentLivingEntity implements Bat {
    private volatile boolean hanging;

    public TridentBat(UUID id, Position spawnLocation) {
        super(id, spawnLocation);

        this.hanging = false;
    }

    @Override
    protected void doEncodeMeta(ProtocolMetadata protocolMeta) {
        protocolMeta.setMeta(16, MetadataType.BYTE,
                hanging ? (byte) 1 : (byte) 0);
    }

    @Override
    public void load(CompoundTag tag) {
        this.hanging = ((ByteTag) tag.getTag("BatFlags")).value() == 1;
    }

    @Override
    public boolean isHanging() {
        return hanging;
    }

    @Override
    public boolean isFlying() {
        return !isHanging();
    }

    @Override
    public boolean isHostile() {
        return false;
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
    public void applyProperties(EntityProperties properties) {

    }

    @Override
    public <T extends Projectile> T launchProjectile(EntityProperties properties) {
        return null;
    }

    @Override
    public EntityType type() {
        return EntityType.BAT;
    }
}
