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
package net.tridentsdk.api.nbt.builder;

import net.tridentsdk.api.nbt.CompoundTag;

/**
 * @author The TridentSDK Team
 */
public class NBTBuilder {
    final CompoundTag base;

    private NBTBuilder(CompoundTag base) {
        this.base = base;
    }

    private NBTBuilder(String name) {
        this(new CompoundTag(name));
    }

    public static CompoundTagBuilder<NBTBuilder> newBase(String name) {
        return new NBTBuilder(name).begin();
    }

    public static CompoundTagBuilder<NBTBuilder> fromBase(CompoundTag tag) {
        return new NBTBuilder(tag).begin();
    }

    private CompoundTagBuilder<NBTBuilder> begin() {
        return new CompoundTagBuilder<>(this.base, this);
    }

    public CompoundTag build() {
        return this.base;
    }
}

