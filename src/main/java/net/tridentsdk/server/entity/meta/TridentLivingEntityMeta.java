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

import net.tridentsdk.meta.entity.living.EntityLivingMeta;
import net.tridentsdk.server.net.EntityMetadata;

import javax.annotation.concurrent.ThreadSafe;

/**
 * @author TridentSDK
 * @since 0.5-alpha
 */
@ThreadSafe
public class TridentLivingEntityMeta extends TridentEntityMeta implements EntityLivingMeta {

    public TridentLivingEntityMeta(EntityMetadata metadata) {
        super(metadata);
        metadata.add(6, EntityMetadata.EntityMetadataType.BYTE, 0b10);
        metadata.add(7, EntityMetadata.EntityMetadataType.FLOAT, 20.0f);
        metadata.add(8, EntityMetadata.EntityMetadataType.VARINT, 0);
        metadata.add(9, EntityMetadata.EntityMetadataType.BOOLEAN, false);
        metadata.add(10, EntityMetadata.EntityMetadataType.VARINT, 0);
    }

    @Override
    public boolean isHandActive() {
        return this.getMetadata().get(6).asBit(0);
    }

    @Override
    public TridentLivingEntityMeta setHandActive(boolean active) {
        this.getMetadata().get(6).setBit(0, active);

        return this;
    }

    @Override
    public boolean isMainHandActive() {
        return !this.getMetadata().get(6).asBit(1);
    }

    @Override
    public TridentLivingEntityMeta setMainHandActive(boolean mainHand) {
        this.getMetadata().get(6).setBit(1, !mainHand);

        return this;
    }

    @Override
    public float getHealth() {
        return this.getMetadata().get(7).asFloat();
    }

    @Override
    public TridentLivingEntityMeta setHealth(float health) {
        this.getMetadata().get(7).set(health);

        return this;
    }

    @Override
    public int getPotionEffectColor() {
        return this.getMetadata().get(8).asInt();
    }

    @Override
    public TridentLivingEntityMeta setPotionEffectColor(int potionEffectColor) {
        this.getMetadata().get(8).set(potionEffectColor);

        return this;
    }

    @Override
    public boolean isPotionEffectAmbient() {
        return this.getMetadata().get(9).asBoolean();
    }

    @Override
    public TridentLivingEntityMeta setPotionEffectAmbient(boolean ambient) {
        this.getMetadata().get(9).set(ambient);

        return this;
    }

    @Override
    public int getNumberOfArrowsInEntity() {
        return this.getMetadata().get(10).asInt();
    }

    @Override
    public TridentLivingEntityMeta setNumberOfArrowsInEntity(int arrows) {
        this.getMetadata().get(10).set(arrows);

        return this;
    }

}
