/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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
import net.tridentsdk.entity.meta.EntityMeta;
import net.tridentsdk.server.net.EntityMetadata;

/**
 * @author TridentSDK
 * @since 0.5-alpha
 */
public class TridentEntityMeta implements EntityMeta {

    @Getter
    private EntityMetadata metadata;

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
        return metadata.get(0).asBit(0);
    }

    @Override
    public void setOnFire(boolean onFire) {
        metadata.get(0).setBit(0, onFire);
    }

    @Override
    public boolean isCrouched() {
        return metadata.get(0).asBit(1);
    }

    @Override
    public void setCrouched(boolean crouched) {
        metadata.get(0).setBit(1, crouched);
    }

    @Override
    public boolean isSprinting() {
        return metadata.get(0).asBit(3);
    }

    @Override
    public void setSprinting(boolean sprinting) {
        metadata.get(0).setBit(3, sprinting);
    }

    @Override
    public boolean isEating() {
        return metadata.get(0).asBit(4);
    }

    @Override
    public void setEating(boolean eating) {
        metadata.get(0).setBit(4, eating);
    }

    @Override
    public boolean isInvisible() {
        return metadata.get(0).asBit(5);
    }

    @Override
    public void setInvisible(boolean invisible) {
        metadata.get(0).setBit(5, invisible);
    }

    @Override
    public boolean isGlowing() {
        return metadata.get(0).asBit(6);
    }

    @Override
    public void setGlowing(boolean glowing) {
        metadata.get(0).setBit(6, glowing);
    }

    @Override
    public boolean isUsingElytra() {
        return metadata.get(0).asBit(7);
    }

    @Override
    public void setUsingElytra(boolean usingElytra) {
        metadata.get(0).setBit(7, usingElytra);
    }

    @Override
    public int getAir() {
        return metadata.get(1).asInt();
    }

    @Override
    public void setAir(int air) {
        metadata.get(1).set(air);
    }

    @Override
    public String getCustomName() {
        return metadata.get(2).asString();
    }

    @Override
    public void setCustomName(String name) {
        metadata.get(2).set(name);
    }

    @Override
    public boolean isCustomNameVisible() {
        return metadata.get(3).asBoolean();
    }

    @Override
    public void setCustomNameVisible(boolean visible) {
        metadata.get(3).set(visible);
    }

    @Override
    public boolean isSilent() {
        return metadata.get(4).asBoolean();
    }

    @Override
    public void setSilent(boolean silent) {
        metadata.get(4).set(silent);
    }

    @Override
    public boolean isNoGravity() {
        return metadata.get(5).asBoolean();
    }

    @Override
    public void setNoGravity(boolean noGravity) {
        metadata.get(5).set(noGravity);
    }

}
