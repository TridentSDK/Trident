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
package net.tridentsdk.server.effect.potion;

import net.tridentsdk.effect.potion.PotionEffect;
import net.tridentsdk.effect.potion.PotionEffectType;
import net.tridentsdk.meta.nbt.NBTField;
import net.tridentsdk.meta.nbt.TagType;

/**
 * Represents a potion effect
 *
 * @author The TridentSDK Team
 */
public class TridentPotionEffect implements PotionEffect {
    @NBTField(name = "id", type = TagType.BYTE)
    protected byte id;
    @NBTField(name = "Amplifier", type = TagType.BYTE)
    protected byte amplifier;
    @NBTField(name = "Duration", type = TagType.INT)
    protected int duration;
    @NBTField(name = "Ambient", type = TagType.BYTE)
    protected byte ambient;
    @NBTField(name = "ShowParticles", type = TagType.BYTE)
    protected byte showParticles;

    @Override
    public PotionEffectType effectType() {
        return PotionEffectType.fromId(id);
    }

    @Override
    public void setEffectType(PotionEffectType type) {
        this.id = type.id();
    }

    @Override
    public byte amplifier() {
        return amplifier;
    }

    @Override
    public void setAmplifier(byte b) {
        this.amplifier = b;
    }

    @Override
    public int duration() {
        return duration;
    }

    @Override
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public boolean isAmbient() {
        return ambient == 1;
    }

    @Override
    public void setAmbient(boolean ambient) {
        this.ambient = (byte) (ambient ? 1 : 0);
    }

    @Override
    public boolean showParticles() {
        return showParticles == 1;
    }

    @Override
    public void setShowParticles(boolean showParticles) {
        this.showParticles = (byte) (showParticles ? 1 : 0);
    }
}
