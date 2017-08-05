/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
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
package net.tridentsdk.server.entity.meta;

import lombok.Getter;
import net.tridentsdk.meta.entity.EntityMeta;
import net.tridentsdk.server.net.EntityMetadata;

import javax.annotation.concurrent.ThreadSafe;

/**
 * @author TridentSDK
 * @since 0.5-alpha
 */
@ThreadSafe
public class TridentEntityMeta implements EntityMeta {
    @Getter
    private final EntityMetadata metadata;

    public TridentEntityMeta(EntityMetadata metadata) {
        this.metadata = metadata;
        this.metadata.add(0, EntityMetadata.EntityMetadataType.BYTE, 0);
        this.metadata.add(1, EntityMetadata.EntityMetadataType.VARINT, 0);
        this.metadata.add(2, EntityMetadata.EntityMetadataType.STRING, "");
        this.metadata.add(3, EntityMetadata.EntityMetadataType.BOOLEAN, false);
        this.metadata.add(4, EntityMetadata.EntityMetadataType.BOOLEAN, false);
        this.metadata.add(5, EntityMetadata.EntityMetadataType.BOOLEAN, false);
    }

    @Override
    public boolean isOnFire() {
        return this.metadata.get(0).asBit(0);
    }

    @Override
    public TridentEntityMeta setOnFire(boolean onFire) {
        this.metadata.get(0).setBit(0, onFire);

        return this;
    }

    @Override
    public boolean isCrouched() {
        return this.metadata.get(0).asBit(1);
    }

    @Override
    public TridentEntityMeta setCrouched(boolean crouched) {
        this.metadata.get(0).setBit(1, crouched);

        return this;
    }

    @Override
    public boolean isSprinting() {
        return this.metadata.get(0).asBit(3);
    }

    @Override
    public TridentEntityMeta setSprinting(boolean sprinting) {
        this.metadata.get(0).setBit(3, sprinting);

        return this;
    }

    @Override
    public boolean isEating() {
        return this.metadata.get(0).asBit(4);
    }

    @Override
    public TridentEntityMeta setEating(boolean eating) {
        this.metadata.get(0).setBit(4, eating);

        return this;
    }

    @Override
    public boolean isInvisible() {
        return this.metadata.get(0).asBit(5);
    }

    @Override
    public TridentEntityMeta setInvisible(boolean invisible) {
        this.metadata.get(0).setBit(5, invisible);

        return this;
    }

    @Override
    public boolean isGlowing() {
        return this.metadata.get(0).asBit(6);
    }

    @Override
    public TridentEntityMeta setGlowing(boolean glowing) {
        this.metadata.get(0).setBit(6, glowing);

        return this;
    }

    @Override
    public boolean isUsingElytra() {
        return this.metadata.get(0).asBit(7);
    }

    @Override
    public TridentEntityMeta setUsingElytra(boolean usingElytra) {
        this.metadata.get(0).setBit(7, usingElytra);

        return this;
    }

    @Override
    public int getAir() {
        return this.metadata.get(1).asInt();
    }

    @Override
    public TridentEntityMeta setAir(int air) {
        this.metadata.get(1).set(air);

        return this;
    }

    @Override
    public String getCustomName() {
        return this.metadata.get(2).asString();
    }

    @Override
    public TridentEntityMeta setCustomName(String name) {
        this.metadata.get(2).set(name);

        return this;
    }

    @Override
    public boolean isCustomNameVisible() {
        return this.metadata.get(3).asBoolean();
    }

    @Override
    public TridentEntityMeta setCustomNameVisible(boolean visible) {
        this.metadata.get(3).set(visible);

        return this;
    }

    @Override
    public boolean isSilent() {
        return this.metadata.get(4).asBoolean();
    }

    @Override
    public TridentEntityMeta setSilent(boolean silent) {
        this.metadata.get(4).set(silent);

        return this;
    }

    @Override
    public boolean isNoGravity() {
        return this.metadata.get(5).asBoolean();
    }

    @Override
    public TridentEntityMeta setNoGravity(boolean noGravity) {
        this.metadata.get(5).set(noGravity);

        return this;
    }
}
