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
package net.tridentsdk.server.player;

import net.tridentsdk.meta.entity.living.PlayerMeta;
import net.tridentsdk.server.entity.meta.TridentLivingEntityMeta;
import net.tridentsdk.server.net.EntityMetadata;

import javax.annotation.concurrent.ThreadSafe;

/**
 * @author TridentSDK
 * @since 0.5-alpha
 */
@ThreadSafe
public class TridentPlayerMeta extends TridentLivingEntityMeta implements PlayerMeta {

    public TridentPlayerMeta(EntityMetadata metadata) {
        super(metadata);
        metadata.add(11, EntityMetadata.EntityMetadataType.FLOAT, 0f);
        metadata.add(12, EntityMetadata.EntityMetadataType.VARINT, 0);
        metadata.add(13, EntityMetadata.EntityMetadataType.BYTE, -1);
        metadata.add(14, EntityMetadata.EntityMetadataType.BYTE, 1);
    }

    @Override
    public float getAdditionalHearts() {
        return this.getMetadata().get(11).asFloat();
    }

    @Override
    public void setAdditionalHearts(float hearts) {
        this.getMetadata().get(11).set(hearts);
    }

    @Override
    public int getScore() {
        return this.getMetadata().get(12).asInt();
    }

    @Override
    public void setScore(int score) {
        this.getMetadata().get(12).set(score);
    }

    @Override
    public byte getSkinFlags() {
        return this.getMetadata().get(13).asByte();
    }

    @Override
    public void setSkinFlags(byte skinFlags) {
        this.getMetadata().get(13).set(skinFlags);
    }

    @Override
    public boolean isCapeEnabled() {
        return this.getMetadata().get(13).asBit(0);
    }

    @Override
    public void setCapeEnabled(boolean enabled) {
        this.getMetadata().get(13).setBit(0, enabled);
    }

    @Override
    public boolean isJacketEnabled() {
        return this.getMetadata().get(13).asBit(1);
    }

    @Override
    public void setJacketEnabled(boolean enabled) {
        this.getMetadata().get(13).setBit(1, enabled);
    }

    @Override
    public boolean isLeftSleeveEnabled() {
        return this.getMetadata().get(13).asBit(2);
    }

    @Override
    public void setLeftSleeveEnabled(boolean enabled) {
        this.getMetadata().get(13).setBit(2, enabled);
    }

    @Override
    public boolean isRightSleeveEnabled() {
        return this.getMetadata().get(13).asBit(3);
    }

    @Override
    public void setRightSleeveEnabled(boolean enabled) {
        this.getMetadata().get(13).setBit(3, enabled);
    }

    @Override
    public boolean isLeftLegPantsEnabled() {
        return this.getMetadata().get(13).asBit(4);
    }

    @Override
    public void setLeftLegPantsEnabled(boolean enabled) {
        this.getMetadata().get(13).setBit(4, enabled);
    }

    @Override
    public boolean isRightLegPantsEnabled() {
        return this.getMetadata().get(13).asBit(5);
    }

    @Override
    public void setRightLegPantsEnabled(boolean enabled) {
        this.getMetadata().get(13).setBit(5, enabled);
    }

    @Override
    public boolean isHatEnabled() {
        return this.getMetadata().get(13).asBit(6);
    }

    @Override
    public void setHatEnabled(boolean enabled) {
        this.getMetadata().get(13).setBit(6, enabled);
    }

    @Override
    public boolean isLeftHandMain() {
        return this.getMetadata().get(14).asByte() == 0;
    }

    @Override
    public void setLeftHandMain(boolean main) {
        this.getMetadata().get(14).set(main ? 0 : 1);
    }

}
